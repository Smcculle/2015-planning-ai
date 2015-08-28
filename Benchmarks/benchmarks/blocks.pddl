(:domain blocks
  (:constants table - object)
  (:action move
    :parameters (?block - block ?from - object ?to - block)
    :precondition (and (on ?block ?from)
                       (clear ?block)
                       (clear ?to))
    :effect (and (on ?block ?to)
                 (clear ?from)
                 (not (on ?block ?from))
                 (not (clear ?to))))
  (:action moveToTable
    :parameters (?block - block ?from - block)
    :precondition (and (on ?block ?from)
                       (clear ?block))
    :effect (and (on ?block table)
                 (clear ?from)
                 (not (on ?block ?from)))))