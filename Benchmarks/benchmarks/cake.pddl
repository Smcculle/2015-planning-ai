(:domain cake
  (:action eat
    :parameters (?object - object)
    :precondition (have ?object)
    :effect (and (not (have ?object))
                 (eaten ?object)))
  (:action bake
    :parameters (?object - object)
    :precondition (not (have ?object))
    :effect (have ?object)))