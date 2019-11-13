#!/usr/bin/python3

# Feature selection
# SelectKBest
# SelectFpr

# 3 ML techniques
# MultinomialNB
# MLP (neural net) 
# k nearest neighbours

import sys
import os
import statistics
from multiprocessing import Process

import numpy as np

import sklearn.model_selection as mSelection
import sklearn.feature_extraction.text as extraction
import sklearn.feature_selection as fSelection
import sklearn.ensemble as ensemble
import sklearn.neural_network as neural

from sklearn.model_selection import ShuffleSplit
from sklearn import metrics
from sklearn.model_selection import cross_val_score

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
    
def verify(pipe, vSet):
    predicted = pipe.predict(vSet['data'])
    accuracy = 0
    for prediction, actual in zip(predicted, vSet['score']):
        if (prediction == actual):
            accuracy += 1

    accuracy = accuracy / len(vSet['score']) 
    print("Accuracy with validation set: " + str(accuracy))

def crossValidate(pipe, trainingSet):
    cv = ShuffleSplit(n_splits=5, test_size=0.2, random_state=0)
    pipe.fit(trainingSet['data'], trainingSet['score'])
    return cross_val_score(pipe, trainingSet['data'], trainingSet['score'], cv=cv)

def runTest(pipe, splits, msg):
    print(msg)
    scores = crossValidate(pipe, splits['trn'])
    print("Crossvalidation mean: "+str(statistics.mean(scores)))
    verify(pipe, splits['val'])

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
    training = {'data': x_alt, 'score': y_alt}
    return {'val': validation, 'trn': training}

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

    print("\nCount Vect -> SelectFpr -> KNearest")
    a = 0.0005
    for i in range(1,7):
        if (i%2==1):
            a = a * 2
        else:
            a = a * 5
        pipe = Pipeline([
            ('vect', extraction.CountVectorizer()),
            ('chi2', fSelection.SelectFpr(fSelection.chi2, alpha=a)),
            ('clf', KNeighborsClassifier()),
        ])
        runTest(pipe, split, "alpha = " + str(a))

    print("\nCount Vect -> fSelect -> MultiNB")
    for i in range(1,7):
        pipe = Pipeline([
            ('vect', extraction.CountVectorizer()),
            ('chi2', fSelection.SelectKBest(fSelection.chi2, k=500*i)),
            ('clf', MultinomialNB()),
        ])
        runTest(pipe, split, str(500*i))

    print("\nCount Vect -> SelectFpr -> MultiNB")
    a = 0.0005
    for i in range(1,7):
        if (i%2==1):
            a = a * 2
        else:
            a = a * 5
        pipe = Pipeline([
            ('vect', extraction.CountVectorizer()),
            ('chi2', fSelection.SelectFpr(fSelection.chi2, alpha=a)),
            ('clf', MultinomialNB()),
        ])
        runTest(pipe, split, "alpha = " + str(a))

    print("\nCount Vect -> fSelect -> MLP")
    for i in range(1,7):
        pipe = Pipeline([
            ('vect', extraction.CountVectorizer()),
            ('chi2', fSelection.SelectKBest(fSelection.chi2, k=500*i)),
            ('clf', neural.MLPClassifier(solver='adam',early_stopping=True, max_iter=400)),
        ])
        runTest(pipe, split, str(500*i))

    print("\nCount Vect -> SelectFpr -> MLP")
    a = 0.0005
    for i in range(1,7):
        if (i%2==1):
            a = a * 2
        else:
            a = a * 5
        pipe = Pipeline([
            ('vect', extraction.CountVectorizer()),
            ('chi2', fSelection.SelectFpr(fSelection.chi2, alpha=a)),
            ('clf', neural.MLPClassifier(solver='adam',early_stopping=True, max_iter=400)),
        ])
        runTest(pipe, split, "alpha = " + str(a))
    

    #My test pipelines 
    '''
    print("")
    a = 0.00005
    print("Count Vect -> SelectFpr -> MLP")
    for i in range(1,10):
        if (i%2==1):
            a = a * 2
        else:
            a = a * 5
        pipe = Pipeline([
            ('vect', extraction.CountVectorizer(ngram_range=(1,2), min_df=10)),
            ('chi2', fSelection.SelectFpr(fSelection.chi2, alpha=a)),
            ('clf', neural.MLPClassifier(solver='adam',early_stopping=True, max_iter=400)),
        ])    
        runTest(pipe, split, "alpha = "+str(a))

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

    pipe = Pipeline([
        ('vect', extraction.CountVectorizer(ngram_range=(1,2), min_df=10)),
        ('chi2', fSelection.SelectFwe(fSelection.chi2, alpha=.01)),
        ('clf', neural.MLPClassifier(solver='adam',early_stopping=True, max_iter=400)),
    ])    
    runTest(pipe, split, "Count Vect -> SelectFwe -> MLP")
    '''