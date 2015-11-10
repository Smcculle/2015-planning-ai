(:domain wumpus
  (:constants player - player
              wumpus - wumpus
              gold - gold
              a1 - square
              a2 - square
              a3 - square
              a4 - square
              b1 - square
              b2 - square
              b3 - square
              b4 - square
              c1 - square
              c2 - square
              c3 - square
              c4 - square
              d1 - square
              d2 - square
              d3 - square
              d4 - square)
  (:action move
    :parameters (?from - square ?to - square)
    :precondition (and (alive player)
                       (at player ?from)
                       (adjacent ?from ?to)
                       (not (at wumpus ?to))
                       (not (pit ?to)))
    :effect (and (at player ?to)
                 (not (at player ?from))))
  (:action eaten
    :parameters (?from - square ?to - square)
    :precondition (and (alive player)
                       (at player ?from)
                       (adjacent ?from ?to)
                       (at wumpus ?to))
    :effect (and (at player ?to)
                 (not (at player ?from))
                 (not (alive player))))
  (:action fall
    :parameters (?from - square ?to - square)
    :precondition (and (alive player)
                       (at player ?from)
                       (adjacent ?from ?to)
                       (pit ?to))
    :effect (and (at player ?to)
                 (not (at player ?from))
                 (not (alive player))))
  (:action grab
    :parameters (?location - square)
    :precondition (and (alive player)
                       (at player ?location)
                       (at gold ?location))
    :effect (and (has player gold)
                 (not (at gold ?location)))))