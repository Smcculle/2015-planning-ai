(:problem sussman
  (:domain blocks)
  (:objects a - block
            b - block
            c - block)
  (:initial (and (on b table)
                 (clear b)
                 (on a table)
                 (on c a)
                 (clear c)))
  (:goal (and (on a b)
              (on b c))))