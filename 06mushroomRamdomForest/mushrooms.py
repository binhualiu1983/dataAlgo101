import numpy as np 
import pandas as pd 
import matplotlib.pyplot as plt
import matplotlib as mpl
import seaborn as sns
from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import GridSearchCV 

#https://blog.csdn.net/weixin_44615820/article/details/89261453
#https://www.cnblogs.com/songzhixue/p/11341440.html pandas入门之Series
# https://blog.csdn.net/LittleHuang950620/article/details/81774402 Python的列表解析式，集合解析式，字典解析式
# https://blog.csdn.net/u012102306/article/details/52228516 Random Forest（sklearn参数详解)
# https://www.jb51.net/article/167347.htm 打印决策树

mushrooms=pd.read_csv('C:\Code\code\dataAlgo101\数据集\mushrooms\mushrooms.csv')
mushrooms.columns=['class','cap-shape','cap-surface','cap-color','ruises','odor','gill-attachment','gill-spacing','gill-size','gill-color','stalk-shape','stalk-root','stalk-surface-above-ring','stalk-surface-below-ring','stalk-color-above-ring','stalk-color-below-ring','veil-type','veil-color','ring-number','ring-type','spore-print-color','population','habitat']
pd.set_option("display.max_columns",500) #让所有列都能加载出来
#print(mushrooms.head(10))
#mushrooms.info()

cap_colors = mushrooms['cap-color'].value_counts() #计算各种颜色的数量
m_height = cap_colors.values.tolist()  #将数组转化为列表形式
cap_colors.axes 
cap_color_labels = cap_colors.axes[0].tolist()  #将各颜色的名称作为横坐标
print(m_height)
print(cap_color_labels)

def autolabel(rects,fontsize=14):
    for rect in rects:
        height = rect.get_height()
        ax.text(rect.get_x() + rect.get_width()/2, 1*height,'%d' % int(height),
                ha='center', va='bottom',fontsize=fontsize)

ind = np.arange(10)  #因为有10个颜色，所以做十个bar
width = 0.7    #设置bar的宽度

#设置颜色
colors = ['#DEB887','#778899','#DC143C','#FFFF99','#f8f8ff','#F0DC82','#FF69B4','#D22D1E','#C000C5','g']
#设置画布大小
fig, ax = plt.subplots(figsize=(10,7)) 
#设置bar的具体参数
cap_colors_bars = ax.bar(ind, m_height , width, color=colors)

#设置横纵坐标轴和标题
ax.set_xlabel("Cap Color", fontsize=20)
ax.set_ylabel('Quantity', fontsize=20)
ax.set_title('Mushroom Cap Color Quantity',fontsize=22)
ax.set_xticks(ind)
ax.set_xticklabels(('brown', 'gray','red','yellow','white','buff','pink','cinnamon','purple','green'),
                  fontsize = 12)
                  
#利用上面这个函数，在每个bar上面附上具体的数值
autolabel(cap_colors_bars)        
plt.show() 

#创建两个列表，分别为各颜色有毒蘑菇的数量和个颜色食用菌的数量
poisonous_cc = [] 
edible_cc = []    

for capColor in cap_color_labels:
    size = len(mushrooms[mushrooms['cap-color'] == capColor].index) #各颜色蘑菇总数
    edibles = len(mushrooms[(mushrooms['cap-color'] == capColor) & (mushrooms['class'] == 'e')].index) #各颜色食用菌的数量
    edible_cc.append(edibles)
    poisonous_cc.append(size-edibles) #总减食用得到有毒的数量
print(edible_cc)
print(poisonous_cc)
                        
                        
width = 0.4
fig, ax = plt.subplots(figsize=(14,8))
edible_bars = ax.bar(ind, edible_cc , width, color='#FFB90F') #画食用菌的bars
#有毒菌在食用菌右侧移动width个单位
poison_bars = ax.bar(ind+width, poisonous_cc , width, color='#4A708B') 

ax.set_xlabel("Cap Color",fontsize=20)
ax.set_ylabel('Quantity',fontsize=20)
ax.set_title('Edible and Poisonous Mushrooms Based on Cap Color',fontsize=22)
ax.set_xticks(ind + width / 2) 
ax.set_xticklabels(('brown', 'gray','red','yellow','white','buff','pink','cinnamon','purple','green'),
                  fontsize = 12)
ax.legend((edible_bars,poison_bars),('edible','poisonous'),fontsize=17)
autolabel(edible_bars, 10)
autolabel(poison_bars, 10)
plt.show()

X=mushrooms.drop('class',axis=1) #Predictors
y=mushrooms['class'] #Response
X=pd.get_dummies(X,columns=X.columns,drop_first=True)
print(X.head())

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=1234)

RF_features= RandomForestClassifier()

#可以通过定义树的各种参数，限制树的大小，防止出现过拟合现象
parameters = {'n_estimators': [100,200,500], 
              'criterion': ['gini'],        
              'max_depth': range(5,10), 
              'min_samples_split': [2,4,6,8],
              'min_samples_leaf': [2,4,6,8,10]
             }

#自动调参，通过交叉验证确定最优参数。
grid_RF = GridSearchCV(RF_features,parameters,cv=10,n_jobs=1)
grid_RF = grid_RF.fit(X_train,y_train)

RF_features = grid_RF.best_estimator_
RF_features.fit(X_train,y_train)

y_pred= RF_features.predict(X_test)
print(RF_features)

importance=RF_features.feature_importances_
series=pd.Series(importance,index=X_train.columns)
plt.figure(figsize = (20,50))
series.sort_values(ascending=True).plot('barh')
plt.show()