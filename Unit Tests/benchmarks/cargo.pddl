(:domain cargo
  (:action load
    :parameters (?cargo - cargo ?plane - plane ?airport - airport)
    :precondition (and (at ?plane ?airport)
                       (at ?cargo ?airport))
    :effect (and (not (at ?cargo ?airport))
                 (in ?cargo ?plane)))
  (:action unload
    :parameters (?cargo - cargo ?plane - plane ?airport - airport)
    :precondition (and (in ?cargo ?plane)
                       (at ?plane ?airport))
    :effect (and (at ?cargo ?airport)
                 (not (in ?cargo ?plane))))
  (:action fly
    :parameters (?plane - plane ?from - airport ?to - airport)
    :precondition (at ?plane ?from)
    :effect (and (not (at ?plane ?from))
                 (at ?plane ?to))))