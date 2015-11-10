(:problem have_eat_cake
  (:domain cake)
  (:objects cake - object)
  (:initial (have cake))
  (:goal (and (have cake)
              (eaten cake))))