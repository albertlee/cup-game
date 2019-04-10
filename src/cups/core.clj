(ns cups.core)

;; 杯子倒水问题
;; 两个指定容量的杯子，获取指定数量的水，求步骤
;; 例如: 一个6L，一个5L，获取3L

;; 动作包括:
;; 倒满 cup-x , 状态: cup-x 变满，另一个cup不变
;; 清空 cup-x , 状态: cup-x 清空，另一个cup不变
;; 转移 cup-x cup-y , 状态: cup-y 变满, cup-x 剩下(cup-Y容量 - cup-y 原有)

;; 算法是一个状态空间搜索，可以用广度优先算法

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

#_(apply distinct?
         (map str (map vec (map (comp #(map :v %) vals)
                                [{:a {:c 6 :v 0} :b {:c 5 :v 0}}
                                 {:a {:c 6 :v 6} :b {:c 5 :v 0}}
                                 {:a {:c 6 :v 0} :b {:c 5 :v 0}}
                                 ]))))


(defn check-state-hist "判断是否在走回头路"
  [state-hist]
  (apply distinct?
         (map str (map vec (map (comp #(map :v %) vals)
                                state-hist)))))

;; (check-state-hist [init-state init-state])

(def found-result (atom false))

(defn search
  [state target path state-hist]

  (when-not (or @found-result
                (> (count path) 12)
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

(search init-state target [] [init-state])
(let [init-state {:a {:c 6 :v 0}
                  :b {:c 5 :v 0}}
      target 3
      init-path ['init]
      init-state-hist [init-state]
      ]
  (reset! found-result false)
  (search init-state target init-path init-state-hist))

(let [init-state {:a {:c 6 :v 0}
                  :b {:c 5 :v 0}}
      target 2
      init-path ['init]
      init-state-hist [init-state]
      ]
  (reset! found-result false)
  (search init-state target init-path init-state-hist))
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
;; 实例
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
