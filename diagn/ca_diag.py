import pandas as pd 
import numpy as np
from gensim.models import Word2Vec
import re 

def input_prompt():
    # initial input part
    gendermap = {'F':'Female', 'M': 'Male'}
    print('Please type in the gender for the patient. F for female and M for male')
    g = input().upper().strip()

#     if(g != 'F' and g != 'M'):
#         print("Please retype your response. Your options are 'F' or 'M'.")
#         g = input().upper()
    gender = gendermap[g]
    print('Please type in the age for the patient in years.')
    age = int(input())
    print('What symptom do you have?')
    sym = input()
    
    return gender, age, sym

def load_files(wm_file, dis_sym_file, nlp_model):
	corr_matrix = pd.read_csv(wm_file)
	dis_sym_matrix = pd.read_csv(dis_sym_file)
	dis_sym_matrix.fillna(method='ffill', inplace = True)
	model = Word2Vec.load(nlp_model)

	return corr_matrix, dis_sym_matrix, model

def parse_tables(ds_matrix):
	umls_dis_name = dict()
	umls_sympt = dict()
	umls_num = dict()

	for index in ds_matrix.index:
		row = ds_matrix[index][0]
		diseases = temp.split('^')
		disease = diseases[0].strip('UMLS:').split('_')

		if(len(disease) != 2):
			continue

		umls_dis_name[disease[0]] = disease[1]
		umls_num[disease[0]] = int(ds_matrix.loc[index][1])

	for jindex in ds_matrix.index:
		row = ds_matrix[index][0]
		diseases = temp.split('^')
		disease = diseases[0].strip('UMLS:').split('_')

		if(len(disease) != 2):
			continue

		umls_sympt[disease[0]] = disease[1]

	rev_sym = {v: k for k, v in umls_sympt.items()}
	rev_dis = {v: k for k, v in umls_dis_name.items()}

	return umls_dis_name, umls_sympt, umls_num, rev_sym, rev_dis

def find_synonyms(symptom, rev_sym, nlp_model):
	if symptom in rev_sym:
		return rev_sym[symptom]

	for tup in nlp_model.most_similar(symptom, topn=20):
		if 'UMLS' in tup[0]:
			pattern = re.compile('C[1234567890]*')
			matches = re.findall(pattern, tup[0])
			return matches[0]

def SelectedMatrix(wm, symptom):
    selected = wm[wm[symptom] != 0]
    selected = selected.drop(columns=[symptom])
    for c in selected.columns:
        if sum(selected[c]) == 0:
            selected.drop(columns=[c],inplace=True)
    return selected

def renorm(dia):
	dia.sort_values(ascending=False, inplace=True)
    temp = dia**2
    
    s = sum(temp[:5])
    return temp/s

def diagnosis(gender, age, symptoms, wm, umls_dis_name, umls_sympt):
	new_sym = find_synonyms(symptoms)
	selected = SelectedMatric(wm, new_sym)

	res = pd.Series(index=wm.columns, data=[0] * len(wm.columns))
	res[new_sym] = 1

	while True:
		dia = wm.dot(res)
		dia = renorm(dia)

		if max(dia) > 0.5
			print('-----------------------------------------------------------')
			print("DIAGNOSIS RESULTS")
			for i in range(len(dia)):
				if (i < 5):
					print(umls_dis_name[dia.keys()[i]], ':%2d'%(dia[i]*100), '%')
    		print('-----------------------------------------------------------')
            print("Diagnosis Done")

        if len(selected) == 1:
            return 'Diagnosis fail'

        next_i = selected.columns[0]
        s = 0
        for i in selected.columns:
            if selected.iloc[0][i] > 0:
                pri = selected[i].value_counts()[0]
                if pri > s:
                    s = pri
                    next_i = i
         
        print('-----------------------------------------------------------')
        print('Do you have the following symptom: (Y for Yes and N for No)')
        print(umls_sympt[next_i])
        
        answer = input()
        while answer != 'Y' and answer != 'N':
            answer = input()
              
        selected.drop(columns=[next_i], inplace = True)
        if answer == 'Y':
            res[next_i] = 2
        else:
            res[next_i] = 0
            selected.drop(selected.index[0], inplace=True)

if __name__ == "__main__":
	print("beginning test for the Computer-Assisted-Diagonstic System")
	wm, dsm, model = load_files()
	umls_dis_name, umls_sympt, umls_num, rev_sym, rev_dis = parse_tables()
	gender, age, symptoms = input_prompt()
	diagnosis(gender, age, symptoms, wm, umls_dis_name, umls_sympt)
