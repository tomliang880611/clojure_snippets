(ns my-tool-kit.concurrency-test)

;;;;;;;;;;;;;;;; future
;; define futher
(do
  (future (Thread/sleep 4000)
          (println "It will be printed after 4 sec"))
  (println "printed now...."))

;; deref/@ or get result from futher
(let
    [result (future
              (pr  intln "this gets executed once...")
              (Thread/sleep 4000)
              5)]
  (println "deref:" (deref result))
  (println "@:" @result))

;; detect if futhure is done running
(realized? (future (Thread/sleep 1000)))
(let [f (future)]
  @f
  (realized? f))


;;;;;;;;;;;;;;;; delay
(def jackson-5-delay
  (delay (let [message "Just call my name and I will be there"]
           (println "First deref:" message)
           message)))

(force jackson-5-delay)

;; the result for delay is also cached
@jackson-5-delay

;; printed only once
(let
    [print-something (delay (println "give me something delicious..."))]
  (force print-something)
  @print-something
  (force print-something))

;; exmaple
(def gimli-headshots ["serious.jpg" "fun.jpg" "playful.jpg"])
(defn email-user
  [email-address]
  (println "Sending headshot notification to" email-address))

(defn upload-document
  [headshot]
  true)

;; the task wrapped by delay is only executed once even
;; is called 3 times
(let
    [notify (delay (email-user "and-my-axe@gmail.com"))]
  (doseq [headshot gimli-headshots]
    (future (upload-document headshot)
            (force notify))))


;;;;;;;;;;;;;;;; promises
(def my-promise (promise))
(deliver my-promise (+ 1 2))
@my-promise

;; exmaple
(def yak-butter-international
  {:store "yak butter international"
   :price 80
   :smoothness 90})

(def butter-than-nothing
  {:sotre "butter than nothing"
   :price 150
   :smoothness 83})

(def baby-got-yak
  {:store "baby got yak"
   :price 94
   :smoothness 99})

(defn mock-api-call
  [result]
  (Thread/sleep 1000)
  result)

(defn satisfactory?
  [butter]
  (and (<= (:price butter) 100)
       (>= (:smoothness butter) 97)
       butter))

;; synchronously
(time
 (some (comp satisfactory? mock-api-call)
       [yak-butter-international butter-than-nothing baby-got-yak]))


;; using futher, promise
(time
 (let [butter-promise (promise)]
   (doseq [butter
           [yak-butter-international butter-than-nothing baby-got-yak]]
     (future
       (if-let [satisfactory-butter (satisfactory? (mock-api-call butter))]
         (deliver butter-promise satisfactory-butter))))
   (println "And the winner is :" @butter-promise)))


;; to prevent promise blocking the thread
(let [p (promise)]
  (deref p 1000 "time out"))

;; callback
(let [ferengi-wisdom-promise (promise)]
  (future (println "Here is some Ferengi wisdom" ferengi-wisdom-promise))
  (Thread/sleep 1000)
  (deliver ferengi-wisdom-promise "Wisper your way to success."))


;;;;;;;;;;;;;;;; queue

(defmacro wait
  "Sleep `timeout` seconds before evaluating body"
  [timeout & body]
  `(do (Thread/sleep ~timeout) ~@body))

(let [saying3 (promise)]
  (future (deliver saying3 (wait 100 "Cheerio!")))
  @(let [saying2 (promise)]
     (future (deliver saying2 (wait 400 "Pip pip!")))
     @(let [saying1 (promise)]
        (future (deliver saying1 (wait 200 "'Ello, gov'na!")))
        (println @saying1)
        saying1)
     (println @saying2)
     saying2)
  (println @saying3)
  saying3)

(defmacro enqueue
  ([q concurrent-promise-name concurrent serialized]
   `(let [~concurrent-promise-name (promise)]
      (future (deliver ~concurrent-promise-name ~concurrent))
      (deref ~q) ;; make sure the sebsequent future is deliver before proceeding
      ~serialized
      ~concurrent-promise-name)) ;; return a promise
  ([concurrent-promise-name concurrent serialized]
   `(enqueue (future) ~concurrent-promise-name ~ concurrent ~serialized)))

(->
 (enqueue saying (wait 200 "'Ello, gov'a!") (println @saying))
 (enqueue saying (wait 400 "Pip, pip!") (println @saying))
 (enqueue saying (wait 100 "Cheerio!") (println @saying)))
