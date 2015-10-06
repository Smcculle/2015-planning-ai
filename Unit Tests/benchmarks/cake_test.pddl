(:problem cake_test
  (:domain cake)
  (:objects Cake - cake)
  (:initial (have Cake))
  (:goal (and (have Cake)
              (eaten Cake))))