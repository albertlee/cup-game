# cups

杯子倒水问题
两个指定容量的杯子，获取指定数量的水，求步骤
例如: 一个6L，一个5L，获取3L

动作包括:
倒满 cup-x , 状态: cup-x 变满，另一个cup不变
清空 cup-x , 状态: cup-x 清空，另一个cup不变
转移 cup-x cup-y , 状态: cup-y 变满, cup-x 剩下(cup-Y容量 - cup-y 原有)

算法是一个状态空间搜索

## 搜索结果实例

### 6,5 -> 3

Action & States:
init
{:a {:c 6, :v 0}, :b {:c 5, :v 0}}
(act-fill :a)
{:a {:c 6, :v 6}, :b {:c 5, :v 0}}
(act-fill :b)
{:a {:c 6, :v 6}, :b {:c 5, :v 5}}
(act-empty :a)
{:a {:c 6, :v 0}, :b {:c 5, :v 5}}
(act-transfer :b :a)
{:a {:c 6, :v 5}, :b {:c 5, :v 0}}
(act-fill :b)
{:a {:c 6, :v 5}, :b {:c 5, :v 5}}
(act-transfer :b :a)
{:a {:c 6, :v 6}, :b {:c 5, :v 4}}
(act-empty :a)
{:a {:c 6, :v 0}, :b {:c 5, :v 4}}
(act-transfer :b :a)
{:a {:c 6, :v 4}, :b {:c 5, :v 0}}
(act-fill :b)
{:a {:c 6, :v 4}, :b {:c 5, :v 5}}
(act-transfer :b :a)
{:a {:c 6, :v 6}, :b {:c 5, :v 3}}


### 6,5 -> 2
Action & States:
init
{:a {:c 6, :v 0}, :b {:c 5, :v 0}}
(act-fill :a)
{:a {:c 6, :v 6}, :b {:c 5, :v 0}}
(act-transfer :a :b)
{:a {:c 6, :v 1}, :b {:c 5, :v 5}}
(act-empty :b)
{:a {:c 6, :v 1}, :b {:c 5, :v 0}}
(act-transfer :a :b)
{:a {:c 6, :v 0}, :b {:c 5, :v 1}}
(act-fill :a)
{:a {:c 6, :v 6}, :b {:c 5, :v 1}}
(act-transfer :a :b)
{:a {:c 6, :v 2}, :b {:c 5, :v 5}}

## Usage

FIXME

## License
996 ONLY License
Copyright © 2019 

Distributed under the 996 ONLY License either version 1.0 or (at
your option) any later version.
