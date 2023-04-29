;Header and description

(define (domain NPUZZLE)
    (:requirements :strips :typing)

    (:types pos square)

    (:predicates
        (on ?x - square ?y - pos)
        (permutable ?x - pos ?y - pos)
        (free ?x - pos)
    )

    (:action slide
        :parameters (?x -square ?y - pos ?z - pos)
        :precondition (and (free ?z) (on ?x ?y) (permutable?y ?z))
        :effect (and (not (free ?z)) (free ?z) (on ?x ?z) (not (on ?x ?y)))
    )

)
