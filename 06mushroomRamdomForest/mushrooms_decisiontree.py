import numpy as np 
import pandas as pd 
import matplotlib.pyplot as plt
import matplotlib as mpl
import seaborn as sns
from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import GridSearchCV 
from sklearn.datasets import load_iris
from sklearn import tree
import pydotplus

mushrooms=pd.read_csv('C:\Code\code\dataAlgo101\数据集\mushrooms\mushrooms.csv')
mushrooms.columns=['class','cap-shape','cap-surface','cap-color','ruises','odor','gill-attachment','gill-spacing','gill-size','gill-color','stalk-shape','stalk-root','stalk-surface-above-ring','stalk-surface-below-ring','stalk-color-above-ring','stalk-color-below-ring','veil-type','veil-color','ring-number','ring-type','spore-print-color','population','habitat']
pd.set_option("display.max_columns",500) #让所有列都能加载出来

X=mushrooms.drop('class',axis=1) #Predictors
y=mushrooms['class'] #Response
X=pd.get_dummies(X,columns=X.columns,drop_first=True)
print(X.head())

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=1234)

#初始化DecisionTreeClassifier
clf = tree.DecisionTreeClassifier(criterion='entropy')
#适配数据
clf = clf.fit(X_train, y_train)
#将决策树以pdf格式可视化

dot_data = tree.export_graphviz(clf, feature_names=X.columns, out_file=None)
graph = pydotplus.graph_from_dot_data(dot_data)
graph.write_pdf("c:/Code/mushrooms.pdf")