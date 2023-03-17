;Header and description

(define (domain COLOR)
    (:requirements :strips :typing)

    (:types state color)

    (:predicates
        (touch ?x - state ?y - state)
        (canbe ?x - state ?y - color)
        (paint ?x - state ?y - color)
        (diff ?x - color ?y - color)
        (done ?x - state ?y - state)
        (clear ?x - state)
        )

    (:action paintstate
        :parameters (?x - state ?y - color)
        :precondition (and (canbe ?x ?y) (clear ?x))
        :effect (and (paint ?x ?y) (not (clear ?x)))
    )

    (:action breaklink
        :parameters (?x -state ?xc - color ?y - state ?yc - color)
        :precondition (and (touch ?x ?y) (paint ?x ?xc) (paint ?y ?yc) (diff ?xc ?yc))
        :effect (done ?x ?y)
    )
)
