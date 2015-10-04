(:domain cake
  (:action bake
    :parameters (?cake - cake)
    :precondition (not (have ?cake))
    :effect (have ?cake))

  (:action eat
    :parameters (?cake - cake)
    :precondition (have ?cake)
    :effect (and (not (have ?cake))
                  (eat ?cake))))