(ns cups.core)

;; 杯子倒水问题
;; 两个指定容量的杯子，获取指定数量的水，求步骤
;; 例如: 一个6L，一个5L，获取3L

;; 动作包括:
;; 倒满 cup-x , 状态: cup-x 变满，另一个cup不变
;; 清空 cup-x , 状态: cup-x 清空，另一个cup不变
;; 转移 cup-x cup-y , 状态: cup-y 变满, cup-x 剩下(cup-Y容量 - cup-y 原有)

;; 算法是一个状态空间搜索

;; 初始状态， 一个6L，一个5L，初始时都是空的
(def init-state {:a {:c 6 :v 0}
                 :b {:c 5 :v 0}})
(def target 3)

(defn act-fill "将 cup-no 倒满
  state: {cup : [容量, 当前值] }
  "
  [state cup-no]
  (let [s (get state cup-no)]
    (assoc state cup-no (assoc s :v (:c s)))))

(defn act-empty "将 cup-no 清空"
  [state cup-no]
  (let [s (get state cup-no)]
    (assoc state cup-no (assoc s :v 0))))

(defn act-transfer "从一个杯子倒入另一个杯子"
  [state cup-from cup-to]
  (let [to-info (get state cup-to)
        trans-max (- (:c to-info) (:v to-info))
        from-info (get state cup-from)
        from-v (:v from-info)
        trans (min trans-max from-v)
        ]
    (-> state
        (assoc cup-from (assoc from-info :v (- (:v from-info) trans)))
        (assoc cup-to (assoc to-info :v (+ (:v to-info) trans))))))

;; 偷懒手工列在这里，其实应该根据给定的 state 里的key来生成这个列表
(def action-list
  '[(act-fill :a)
    (act-fill :b)
    (act-empty :a)
    (act-empty :b)
    (act-transfer :a :b)
    (act-transfer :b :a)])

(defn is-target?
  [state target]
  (some #(= % target)
        (map :v (vals state))))

(defn check-state-hist "判断是否在走回头路, 判断状态历史里是否有重复的，增加这个判断可以极大的控制搜索空间，有效剪枝"
  [state-hist]
  (apply distinct?
         (map str (map vec (map (comp #(map :v %) vals)
                                state-hist)))))

;; 全局状态，用于从递归中跳出，应该有更优雅的方法，懒得写了
(def found-result (atom false))
(def max-deep 12)

(defn search "递归搜索，采用深度优先算法"
  [state target path state-hist]
  (when-not (or @found-result
                (> (count path) max-deep)
                (not (check-state-hist state-hist)) ;; 剪枝，避免走回头路
                )
    (if (is-target? state target)
      (do
        (reset! found-result true)
        (println "Action & States:")
        (doseq [s (reverse (interleave state-hist path))]
          (println s))
        path)

      (doseq [act action-list]
        (let [cmd (cons (first act) (cons state (rest act)))]
          (let [new-state (eval cmd)]
            (search new-state target (cons act path) (cons new-state state-hist))))))))

;; 搜索 6,5 -> 3 的问题:
(let [init-state {:a {:c 6 :v 0}
                  :b {:c 5 :v 0}}
      target 3
      init-path ['init]
      init-state-hist [init-state]
      ]
  (reset! found-result false)
  (search init-state target init-path init-state-hist))

;; 搜索 6,5 -> 2 的问题:
(let [init-state {:a {:c 6 :v 0}
                  :b {:c 5 :v 0}}
      target 2
      init-path ['init]
      init-state-hist [init-state]
      ]
  (reset! found-result false)
  (search init-state target init-path init-state-hist))


;; 人工得到的一个答案， 比暴力搜索得到的要短
;; 6,5 -> 3
#_(-> init-state
      (act-fill :a)
      (act-transfer :a :b)
      (act-empty :b)
      (act-transfer :a :b)
      (act-fill :a)
      (act-transfer :a :b)
      (act-empty :b)
      (act-transfer :a :b)
      (act-fill :a)
      (act-transfer :a :b)
      )

"
;; 搜索结果实例
;; 6,5 -> 3

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


;; 6,5 -> 2
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
"
