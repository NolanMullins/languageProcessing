#!/usr/bin/python3

import sys
import os

import numpy as np

from sklearn import datasets
from sklearn.datasets.base import Bunch
from sklearn.feature_selection import RFE
import sklearn.model_selection as model_selection

#def featureSelection():

def analyzeDataSet(dataSet):
    print(len(dataSet))

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
    x_alt, x_val, y_alt, y_val = model_selection.train_test_split(x,y, train_size=.85, test_size=.15, random_state=135)
    validation = {'data': x_val, 'score': y_val}

    x_train, x_test, y_train, y_test = model_selection.train_test_split(x_alt, y_alt, train_size=.8, test_size=.2, random_state=52)
    training = {'data': x_train, 'score': y_train}
    testing = {'data': x_test, 'score': y_test}
    return {'val': validation, 'trn': training, 'test': testing}

if __name__ == "__main__":
    data = loadFiles()
    split = splitData(data)
    analyzeDataSet(split['val']['x'])
    print(split['val']['y'][0])