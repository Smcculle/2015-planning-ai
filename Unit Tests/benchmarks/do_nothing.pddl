(:problem do_nothing
  (:domain blocks)
  (:objects a - block)
  (:initial (and (on a table)
                 (clear a)))
  (:goal (on a table)))