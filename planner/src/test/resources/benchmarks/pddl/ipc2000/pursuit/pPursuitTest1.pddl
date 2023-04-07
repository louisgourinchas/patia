(define (problem PURSUIT-1-0)
(:domain PURSUIT)
(:objects
    i1 - intruder
    p1 p1 - pursuer
    x1 x2 x3 x4 x5 - vertex
)

(:INIT
    ;links
    (link x1 x3)
    (link x3 x1)
    (link x2 x3)
    (link x3 x2)
    (link x4 x3)
    (link x3 x4)
    (link x5 x3)
    (link x3 x5)
    (link x4 x5)
    (link x5 x4)
    ;starting positions
    (ison i1 x4)
    (ison p1 x2)
    (ison p2 x2)
 )

(:goal
    (OR (AND ()()))
)
)
