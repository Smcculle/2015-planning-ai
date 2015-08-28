(:problem reverse_2
  (:domain blocks)
  (:objects a - block
            b - block)
  (:initial (and (clear a)
                 (on a b)
                 (on b table)))
  (:goal (and (on a table)
              (on b a))))