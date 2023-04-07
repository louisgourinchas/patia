;Header and description

(define (domain PUTSUIT)
    (:requirements :strips :typing)

    (:types vertex pursuer intruder)

    (:predicates
        (link ?x - vertex ?y - vertex)
        (ison ?x - pursuer ?y - vertex)
        (ison ?x - intruder ?y - vertex)
        )

    (:action moveintruder
        :parameters (?x - vertex ?y - vertex ?z - intruder)
        :precondition (and (ison ?z ?x) (link ?x ?y))
        :effect (and (ison ?z ?y) (not (ison ?z ?x)))
    )

    (:action movepursuer
        :parameters (?x - vertex ?y - vertex ?z - pursuer)
        :precondition (and (ison ?z ?x) (link ?x ?y))
        :effect (and (ison ?z ?y) (not (ison ?z ?x)))
    )
)
