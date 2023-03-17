;Header and description

(define (domain sokoban)
    (:requirements :strips :typing)

    ;note: technically player is useless and we could just go (isplayer ?x - pos)
    (:types pos player)

    (:predicates
        (isempty ?x - pos)
        (iscrate ?x - pos)
        (iswall ?x - pos)
        (ison ?x - player ?y - pos)
        ;should be directional somehow, maybe define BOTH link a b and link b a
        (Vlink ?x - pos ?y - pos)
        (Hlink ?x - pos ?y - pos)
    )

    ;player moving from one empty position to another empty position
    ;verticaly
    (:action Vmove
        :parameters (?x -player ?y -pos ?z -pos)
        :precondition (and (isempty ?z) (ison ?x ?y) (Vlink ?y ?z))
        :effect (and (isempty ?y) (not (isempty ?z)) (ison ?x ?z) (not (ison ?x ?y)))
    )

    ;horizontaly
    (:action Hmove
        :parameters (?x -player ?y -pos ?z -pos)
        :precondition (and (isempty ?z) (ison ?x ?y) (Hlink ?y ?z))
        :effect (and (isempty ?y) (not (isempty ?z)) (ison ?x ?z) (not (ison ?x ?y)))
    )

    ;player pushing a crate (player on x, crate on y, 'goal' on z)
    ;verticaly
    (:action Vpush
        :parameters (?w -player ?x -pos ?y -pos ?z -pos)
        :precondition (and 
            (ison ?w ?x)  
            (iscrate ?y)
            (isempty ?z)
            (Vlink ?x ?y)
            (Vlink ?y ?z)
        )
        :effect (and 
            ;x now empty, player moved
            (isempty ?x)
            (not (ison ?w ?x))
            ;y now has player, no more crate
            (ison ?w ?y)
            (not (iscrate ?y))
            ;z now has crate, no longer empty
            (iscrate ?z)
            (not (isempty ?z))
        )
    )

    ;horizontaly
    (:action Hpush
        :parameters (?w -player ?x -pos ?y -pos ?z -pos)
        :precondition (and 
            (ison ?w ?x)  
            (iscrate ?y)
            (isempty ?z)
            (Hlink ?x ?y)
            (Hlink ?y ?z)
        )
        :effect (and 
            ;x now empty, player moved
            (isempty ?x)
            (not (ison ?w ?x))
            ;y now has player, no more crate
            (ison ?w ?y)
            (not (iscrate ?y))
            ;z now has crate, no longer empty
            (iscrate ?z)
            (not (isempty ?z))
        )
    )

)
