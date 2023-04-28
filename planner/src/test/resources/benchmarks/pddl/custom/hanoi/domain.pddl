;Header and description

(define (domain HANOI)
    (:requirements :strips :typing)

    (:types ring spike)

    (:predicates
        (on ?x - ring ?y - ring);is ring x on ring y
        (bigger ?x - ring ?y - ring);is ring x bigger than ring y
        (handempty);is the hand empty
        (holding ?x - ring);is the hand holding ring x
        (clear ?x - ring);is ring x clear (nothing on top)
        (clearspike ?x - spike);is the spike x clear (no ring on it)
        (onspike ?x - ring ?y - spike);is ring x the topmost ring of spike y
    )

    (:action pickup
        :parameters (?x - ring ?y - spike)
        :precondition (and (onspike ?x ?y) (handempty) (clear ?x))
        :effect (and (not (handempty)) (holding ?x) (clearspike ?y) (not (clear ?x)))
    )

    (:action unstack
        :parameters (?x - ring ?y - ring)
        :precondition (and (clear ?x) (handempty) (on ?x ?y))
        :effect (and (not (clear ?x)) (not (handempty)) (holding ?x) (not (on ?x ?y)) (clear ?y))
    )

    (:action putdown
        :parameters (?x -ring ?y - spike)
        :precondition (and (holding ?x) (clearspike ?y))
        :effect (and (handempty) (not (holding ?x)) (onspike ?x ?y) (clear ?x) (not (clearspike ?y)))
    )

    (:action stack
        :parameters (?x - ring ?y - ring)
        :precondition (and (holding ?x) (bigger ?y ?x) (clear ?y) )
        :effect (and (handempty) (not (holding ?x)) (not (clear ?y)) (clear ?x) (on ?x ?y))
    )
)
