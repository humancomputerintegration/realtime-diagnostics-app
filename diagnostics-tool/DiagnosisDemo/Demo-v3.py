# -*- coding: utf-8 -*-
"""
Created on Sat Jul  6 11:54:59 2019

@author: zhang
"""

# -------------------------------Loading data------------------------
import pandas as pd
import numpy as np

WM = pd.read_csv('../WeightMatrix/Dis_Sym_30.csv', index_col=0)

dic = pd.read_csv('../WordEmbedding/trimmed_wv/Dictionary.csv', header = None)

dis2sym = pd.read_csv('../UMLS/dis_symptom.csv', header=None)

dis2sym.fillna(method='ffill',inplace=True)

umls_dis = {}
umls_sym = {}
dis_num = {}
for i in dis2sym.index:
    temp = dis2sym.loc[i][0]
    items = temp.split('^')
    item = items[0].strip('UMLS:').split('_')
    if len(item) != 2: continue
    umls_dis[item[0]] = item[1]
    dis_num[item[0]] = int(dis2sym.loc[i][1])
for i in dis2sym.index:
    temp = dis2sym.loc[i][2]
    items = temp.split('^')
    item = items[0].strip('UMLS:').split('_')
    if len(item) != 2: continue
    umls_sym[item[0]] = item[1]
    
rev_sym = {v: k for k, v in umls_sym.items()}
rev_dis = {v: k for k, v in umls_dis.items()}

# -------------------------------Functions------------------------
def initial_input():
    # initial input part
    gendermap = {'F':'Female', 'M': 'Male'}
    print('Please type in the gender for the patient. F for female and M for male')
    g = input()
    gender = gendermap[g]
    print('Please type in the age for the patient in years.')
    age = int(input())
    print('What symptom do you have?')
    sym = input()
    
    return gender, age, sym

def SelectedMatrix(sym):
    selected = WM[WM[sym] != 0]
    selected = selected.drop(columns=[sym])
    for c in selected.columns:
        if sum(selected[c]) == 0:
            selected.drop(columns=[c],inplace=True)
    return selected

def renorm(dia):
    for c in dia.index:
        dia[c] *= dis_num[c]**(1/3)
    dia.sort_values(ascending=False, inplace=True)
    temp = dia**3
    
    s = sum(temp[:5])
    return temp/s

def symptom(sym_in):
    if sym_in in rev_sym:
        return rev_sym[sym_in]
    for sym in rev_sym:
        if sym_in in sym:
            return rev_sym[sym]
    if sym_in in dic:
        return rev_sym[dic[sym]]
    return 'Symptom not found'

def diagnosis():
    
    gender, age, sym_in = initial_input()
    
    
    sym = symptom(sym_in)
    if sym == 'Symptom not found':
        print(sym)
        return -1
    
    selected = SelectedMatrix(sym)
    
    #The response vector
    res = pd.Series(index=WM.columns, data=[0]*len(WM.columns))
    res[sym] = 1
    
    #Diagnosis process
    while True:
        dia = WM.dot(res)
        
        for j in selected.columns:
            if 0 not in selected[j].value_counts():
                res[j] = 1
                selected.drop(columns=[j], inplace = True)  
                
        if len(selected) == 1 or len(selected.columns) <= 1:
            dia = renorm(dia)
            print('-----------------------------------------------------------')
            print('Diagnosis results:')
            for i in range(len(dia)):
                if i < 5:
                    print(umls_dis[dia.keys()[i]], ':%2d'%(dia[i]*100), '%')
            print('-----------------------------------------------------------')        
            return 'Diagnosis done'
        
                   
        #choose the most relevant symptom to ask: The symptom that are least shared with other diseases
        next_i = selected.columns[0]
        s = 100
        for i in selected.columns:   
            if 0 in selected[i].value_counts():
                pri = abs(selected[i].value_counts()[0] - len(selected)/2)
                if pri < s:
                    s = pri
                    next_i = i      
            else:
                res[next_i] = 1
                selected = selected[selected[next_i]!=0]
         
        print('-----------------------------------------------------------')
        print('Do you have the following symptom: (Y for Yes and N for No)')
        print(umls_sym[next_i])
        
        answer = input()
        while answer != 'Y' and answer != 'N':
            answer = input()
              
        if answer == 'Y':
            res[next_i] = 1
            selected = selected[selected[next_i]!=0]
        else:
            res[next_i] = 0
            selected = selected[selected[next_i]==0]
            
        selected.drop(columns=[next_i], inplace = True)
 
diagnosis()