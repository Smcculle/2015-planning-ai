(:problem easy_stack
  (:domain blocks)
  (:objects a - block
            b - block)
  (:initial (and (on a table)
                 (clear a)
                 (on b table)
                 (clear b)))
  (:goal (on a b)))