(:problem cake_test
  (:domain cake)
  (:objects Cake - cake)
  (:initial (have Cake))
  (:goal (and (have Cake)
              (eat Cake))))