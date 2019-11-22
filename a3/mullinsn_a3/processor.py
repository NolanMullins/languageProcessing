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
import nltk
import numpy as np
import string

import sklearn.model_selection as mSelection
import sklearn.feature_extraction.text as extraction
import sklearn.feature_selection as fSelection
import sklearn.ensemble as ensemble
import sklearn.neural_network as neural

from sklearn.model_selection import StratifiedShuffleSplit
from sklearn import metrics
from sklearn.model_selection import cross_val_score

from sklearn import datasets
from sklearn.datasets.base import Bunch
from sklearn.pipeline import Pipeline
from sklearn.naive_bayes import MultinomialNB
from sklearn.feature_extraction.text import TfidfTransformer
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.neighbors import KNeighborsClassifier
from sklearn.metrics import f1_score

from nltk import word_tokenize          
from nltk.stem.porter import PorterStemmer

#Testing
from sklearn.preprocessing import MinMaxScaler

#Stemming
stemmer = PorterStemmer()
analyzer = CountVectorizer().build_analyzer()
#nltk.download('punkt')

def stemmed_words(doc):
    return (stemmer.stem(w) for w in analyzer(doc))

def stemDoc(doc):
    data = ""
    for w in doc.split():
        data += stemmer.stem(w) +" "
    return data

def analyzeDataSet(dataSet):
    print(len(dataSet['data']))
    
def verify(pipe, vSet):
    predicted = pipe.predict(vSet['data'])
    score = f1_score(vSet['score'], predicted, average='weighted')
    print("FScore on validation set: " + str(score))


#todo set stratified
def crossValidate(pipe, trainingSet):
    cv = StratifiedShuffleSplit(n_splits=5, test_size=0.2, random_state=0)
    pipe.fit(trainingSet['data'], trainingSet['score'])
    #Setting cv = 5, forces it to use a stratified 5 fold
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
    x_alt, x_val, y_alt, y_val = mSelection.train_test_split(x,y, train_size=.85, test_size=.15, random_state=135, stratify=y)
    validation = {'data': x_val, 'score': y_val}
    training = {'data': x_alt, 'score': y_alt}
    return {'val': validation, 'trn': training}

def runFullTest(split):
    print("\nCount Vect -> fSelect -> KNearest")
    for i in range(1,7):
        pipe = Pipeline([
            ('vect', extraction.CountVectorizer(stop_words='english')),
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
            ('vect', extraction.CountVectorizer(stop_words='english')),
            ('chi2', fSelection.SelectFpr(fSelection.chi2, alpha=a)),
            ('clf', KNeighborsClassifier()),
        ])
        runTest(pipe, split, "alpha = " + str(a))

    print("\nCount Vect -> fSelect -> MultiNB")
    for i in range(1,7):
        pipe = Pipeline([
            ('vect', extraction.CountVectorizer(stop_words='english')),
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
            ('vect', extraction.CountVectorizer(stop_words='english')),
            ('chi2', fSelection.SelectFpr(fSelection.chi2, alpha=a)),
            ('clf', MultinomialNB()),
        ])
        runTest(pipe, split, "alpha = " + str(a))

    print("\nCount Vect -> fSelect -> MLP")
    for i in range(1,7):
        pipe = Pipeline([
            ('vect', extraction.CountVectorizer(stop_words='english')),
            ('chi2', fSelection.SelectKBest(fSelection.chi2, k=500*i)),
            ('clf', neural.MLPClassifier()),
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
            ('vect', extraction.CountVectorizer(stop_words='english')),
            ('chi2', fSelection.SelectFpr(fSelection.chi2, alpha=a)),
            ('clf', neural.MLPClassifier()),
        ])
        runTest(pipe, split, "alpha = " + str(a))

def metrics(data):
    vec = CountVectorizer()
    res = vec.fit_transform(data.data)
    print('Unique terms: ' + str(len(vec.get_feature_names())))
    #vec = CountVectorizer(stop_words='english', analyzer=stemmed_words)
    #vec.fit_transform(data.data)
    #print('Unique stems: ' + str(len(vec.get_feature_names())))
    terms = res.toarray()
    tmp = []
    for i in range(len(terms)):
        tmp.append(sum(terms[i]))
    print("Avg doc length: " + str(statistics.mean(tmp)))
    print("Min doc length: " + str(min(tmp)))
    print("Max doc length: " + str(max(tmp)))


if __name__ == "__main__":

    data = loadFiles()
    metrics(data)
    split = splitData(data)
    proc = []

    #runFullTest(split)

    #My test pipelines 
    #model tuning, add bi grams and tri grams
    '''
    pipe = Pipeline([
        ('vect', extraction.CountVectorizer(stop_words='english')),
        ('chi2', fSelection.SelectFpr(fSelection.chi2, alpha=.1)),
        ('clf', neural.MLPClassifier(batch_size=200, solver='adam',early_stopping=True, max_iter=400, learning_rate='adaptive', activation='relu', hidden_layer_sizes=(100,))),
    ])
    runTest(pipe, split, "")
    pipe = Pipeline([
        ('vect', extraction.CountVectorizer(stop_words='english')),
        ('tfid', extraction.TfidfTransformer()),
        ('chi2', fSelection.SelectKBest(fSelection.chi2, k=3000)),
        ('clf', neural.MLPClassifier(solver='adam',early_stopping=True, max_iter=400, learning_rate='adaptive')),
    ])
    runTest(pipe, split, "")
    '''