(:problem reverse_12
  (:domain blocks)
  (:objects a - block
            b - block
            c - block
            d - block
            e - block
            f - block
            g - block
            h - block
            i - block
            j - block
            k - block
            l - block)
  (:initial (and (clear a)
                 (on a b)
                 (on b c)
                 (on c d)
                 (on d e)
                 (on e f)
                 (on f g)
                 (on g h)
                 (on h i)
                 (on i j)
                 (on j k)
                 (on k l)
                 (on l table)))
  (:goal (and (on a table)
              (on b a)
              (on c b)
              (on d c)
              (on e d)
              (on f e)
              (on g f)
              (on h g)
              (on i h)
              (on j i)
              (on k j)
              (on l k))))