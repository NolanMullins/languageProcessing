#!/usr/bin/python3

#god bless
#https://scikit-learn.org/stable/tutorial/text_analytics/working_with_text_data.html?fbclid=IwAR2UR0n1ZsjhzLJjRedkVA1NpUUjsuf-_ZcVLSSHfbLUIaxEs3-Sg3beBuM#building-a-pipeline

import sys
import os

import numpy as np

import sklearn.model_selection as mSelection
import sklearn.feature_extraction.text as extraction
import sklearn.feature_selection as fSelection

from sklearn import datasets
from sklearn.datasets.base import Bunch
from sklearn.feature_selection import RFE
from sklearn.svm import SVR
from sklearn.pipeline import Pipeline
from sklearn.naive_bayes import MultinomialNB
from sklearn.feature_extraction.text import TfidfTransformer
from sklearn.feature_extraction.text import CountVectorizer


#def featureSelection():

def analyzeDataSet(dataSet):
    print(len(dataSet['data']))
    
def verify(pipe, vSet):
    '''
    counts = transformer.transform(vSet['data'])
    newtfidf = tfidf.transform(counts)
    predicted = clf.predict(newtfidf)
    '''
    predicted = pipe.predict(vSet['data'])

    accuracy = 0
    for prediction, actual in zip(predicted, vSet['score']):
        if (prediction == actual):
            accuracy += 1

    accuracy = accuracy / len(vSet['score']) 
    print("Accuracy: " + str(accuracy))

def train(pipe, trainingSet):
    pipe.fit(trainingSet['data'], trainingSet['score'])

    '''
    cvect = extraction.CountVectorizer()
    trainCounts = cvect.fit_transform(trainingSet['data'])
    tfidf = TfidfTransformer().fit_transform(trainCounts)
    clf = MultinomialNB().fit(tfidf, trainingSet['data'])
    '''
    return pipe

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

    #build pipeline
    pipeCHI = Pipeline([
        ('vect', extraction.CountVectorizer()),
        ('chi2', fSelection.SelectKBest(fSelection.chi2, k=1000)),
        ('clf', MultinomialNB()),
    ])
    pipeCHI = train(pipeCHI, split['trn'])
    verify(pipeCHI, split['val'])
    pipeCHI2 = Pipeline([
        ('vect', extraction.CountVectorizer()),
        ('chi2', fSelection.SelectKBest(fSelection.chi2, k=2000)),
        ('clf', MultinomialNB()),
    ])
    pipeCHI2 = train(pipeCHI2, split['trn'])
    verify(pipeCHI2, split['val'])

    pipeB = Pipeline([
        ('vect', CountVectorizer()),
        ('tfidf', TfidfTransformer()),
        ('clf', MultinomialNB()),
    ])
    pipeB = train(pipeB, split['trn'])
    verify(pipeB, split['val'])
