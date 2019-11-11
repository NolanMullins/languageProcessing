#!/usr/bin/python3

# 3 ML techniques
# MultinomialNB
# MLP (neural net) 
# k nearest neighbours

import sys
import os
from multiprocessing import Process

import numpy as np

import sklearn.model_selection as mSelection
import sklearn.feature_extraction.text as extraction
import sklearn.feature_selection as fSelection
import sklearn.ensemble as ensemble
import sklearn.neural_network as neural

from sklearn import datasets
from sklearn.datasets.base import Bunch
from sklearn.pipeline import Pipeline
from sklearn.naive_bayes import MultinomialNB
from sklearn.feature_extraction.text import TfidfTransformer
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.neighbors import KNeighborsClassifier



#def featureSelection():

def analyzeDataSet(dataSet):
    print(len(dataSet['data']))
    
def verify(pipe, vSet, msg):
    predicted = pipe.predict(vSet['data'])
    accuracy = 0
    for prediction, actual in zip(predicted, vSet['score']):
        if (prediction == actual):
            accuracy += 1

    accuracy = accuracy / len(vSet['score']) 
    print(msg)
    print("Accuracy: " + str(accuracy))

def train(pipe, trainingSet):
    pipe.fit(trainingSet['data'], trainingSet['score'])
    return pipe

def runTest(pipe, splits, msg):
    pipe = train(pipe, splits['trn'])
    verify(pipe, splits['val'], msg)

def loadFiles(): 
    posFiles = os.listdir('txt_sentoken/pos')
    negFiles = os.listdir('txt_sentoken/neg')
    data = Bunch(data=[], score=[], score_names=['pos', 'neg'], file=[])

    for posFile, negFile in zip(posFiles, negFiles):
        with open('txt_sentoken/pos/'+posFile, 'r') as posFile, \
            open('txt_sentoken/neg/'+negFile, 'r') as negFile:
                data['data'].append(''.join(posFile.readlines()))
                data['data'].append(''.join(negFile.readlines()))
                data['score'] = data['score'] + ['pos', 'neg']
                data['file'] = data['file'] + [posFile, negFile]
    return data

def splitData(data):
    x,y=data.data,data.score
    x_alt, x_val, y_alt, y_val = mSelection.train_test_split(x,y, train_size=.85, test_size=.15, random_state=135)
    validation = {'data': x_val, 'score': y_val}

    x_train, x_test, y_train, y_test = mSelection.train_test_split(x_alt, y_alt, train_size=.8, test_size=.2, random_state=52)
    training = {'data': x_train, 'score': y_train}
    testing = {'data': x_test, 'score': y_test}
    return {'val': validation, 'trn': training, 'test': testing}

if __name__ == "__main__":

    data = loadFiles()
    split = splitData(data)
    proc = []

    print("\nCount Vect -> fSelect -> KNearest")
    for i in range(1,7):
        pipe = Pipeline([
            ('vect', extraction.CountVectorizer()),
            ('chi2', fSelection.SelectKBest(fSelection.chi2, k=500*i)),
            ('clf', KNeighborsClassifier()),
        ])
        runTest(pipe, split, str(i*500))

    print("\nCount Vect -> fSelect -> MultiNB")
    for i in range(1,7):
        pipe = Pipeline([
            ('vect', extraction.CountVectorizer()),
            ('chi2', fSelection.SelectKBest(fSelection.chi2, k=500*i)),
            ('clf', MultinomialNB()),
        ])
        runTest(pipe, split, str(500*i))

    print("\nCount Vect -> fSelect -> MLP")
    for i in range(1,7):
        pipe = Pipeline([
            ('vect', extraction.CountVectorizer()),
            ('chi2', fSelection.SelectKBest(fSelection.chi2, k=500*i)),
            ('clf', neural.MLPClassifier(solver='adam',early_stopping=True, max_iter=400)),
        ])
        runTest(pipe, split, str(500*i))

    #My test pipelines 
    print("")
    pipe = Pipeline([
        ('vect', extraction.CountVectorizer(ngram_range=(1,2), min_df=10)),
        ('tfid', extraction.TfidfTransformer()),
        ('clf', MultinomialNB())
    ])
    runTest(pipe, split, "Count Vect -> Tfid -> MultiNB")

    pipe = Pipeline([
        ('vect', extraction.CountVectorizer(ngram_range=(1,2), min_df=10)),
        ('chi2', fSelection.SelectFpr(fSelection.chi2, alpha=.1)),
        ('clf', neural.MLPClassifier(solver='adam',early_stopping=True, max_iter=400)),
    ])    
    runTest(pipe, split, "Count Vect -> SelectFpr -> MLP")
