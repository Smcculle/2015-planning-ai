(:domain rocket
  (:action load
    :parameters (?cargo - cargo ?rocket - rocket ?location - location)
    :precondition (and (at ?rocket ?location)
                       (at ?cargo ?location))
    :effect (and (not (at ?cargo ?location))
                 (in ?cargo ?rocket)))
  (:action unload
    :parameters (?cargo - cargo ?rocket - rocket ?location - location)
    :precondition (and (in ?cargo ?rocket)
                       (at ?rocket ?location))
    :effect (and (at ?cargo ?location)
                 (not (in ?cargo ?rocket))))
  (:action fly
    :parameters (?rocket - rocket ?from - location ?to - location)
    :precondition (at ?rocket ?from)
    :effect (and (not (at ?rocket ?from))
                 (at ?rocket ?to))))