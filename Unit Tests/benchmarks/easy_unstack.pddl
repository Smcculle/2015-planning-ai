(:problem easy_unstack
  (:domain blocks)
  (:objects a - block
            b - block)
  (:initial (and (on a b)
                 (clear a)
                 (on b table)))
  (:goal (on a table)))