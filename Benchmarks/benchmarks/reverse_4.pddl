(:problem reverse_4
  (:domain blocks)
  (:objects a - block
            b - block
            c - block
            d - block)
  (:initial (and (clear a)
                 (on a b)
                 (on b c)
                 (on c d)
                 (on d table)))
  (:goal (and (on a table)
              (on b a)
              (on c b)
              (on d c))))