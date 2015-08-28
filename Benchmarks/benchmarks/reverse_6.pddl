(:problem reverse_6
  (:domain blocks)
  (:objects a - block
            b - block
            c - block
            d - block
            e - block
            f - block)
  (:initial (and (clear a)
                 (on a b)
                 (on b c)
                 (on c d)
                 (on d e)
                 (on e f)
                 (on f table)))
  (:goal (and (on a table)
              (on b a)
              (on c b)
              (on d c)
              (on e d)
              (on f e))))