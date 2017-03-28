(ns my-tool-kit.datetypes)

(import [java.lang.annotation
         Retention
         RetentionPolicy
         Target ElementType]
        [javax.xml.ws
         WebServiceRef
         WebServiceRefs])

(definterface Foo (foo []))

;; annotation on type
(deftype
    ^{Deprecated true
      Retention RetentionPolicy/RUNTIME
      javax.annotation.processing.SupportedOptions ["foo" "bar" "baz"]
      javax.xml.ws.soap.Addressing {:enabled false :required true}
      WebServiceRefs [(WebServiceRef {:name "fred" :type String})
                           (WebServiceRef {:name "ethel" :mappedName "lucy1"})]}
    Bar [^int a
         ;; on field
         ^{:tag int
           Deprecated true
           Retention RetentionPolicy/RUNTIME
           javax.annotation.processing.SupportedOptions ["foo" "bar" "baz"]
           javax.xml.ws.soap.Addressing {:enabled false :required true}
           WebServiceRefs [(WebServiceRef {:name "fred" :type String})
                           (WebServiceRef {:name "ethel" :mappedName "lucy2"})]}
         b]
  ;; on method
  Foo (^{Deprecated true
         Retention RetentionPolicy/RUNTIME
         javax.annotation.processing.SupportedOptions ["foo" "bar" "baz"]
         javax.xml.ws.soap.Addressing {:enabled false :required true}
         WebServiceRefs [(WebServiceRef {:name "fred" :type String})
                         (WebServiceRef {:name "ethel" :mappedName "lucy3"})]}
       foo [this] 42))

;;; There are issues calling the following
;;; two methods.

;;; (.getAnnotation Bar)
;;; (.getAnnotation (.getField Bar "b"))
(.getField Bar "b")
