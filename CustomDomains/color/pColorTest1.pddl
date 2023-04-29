(define (problem COLOR-1-0)
(:domain COLOR)
(:objects
    x1 x2 x3 x4 x5 - state
    a b c - color
)

(:INIT
    ;clear states
    (clear x1) (clear x2) (clear x3) (clear x4) (clear x5)
    ;domains
    (canbe x1 a) (canbe x1 b) (canbe x1 c)
    (canbe x2 a) (canbe x2 b) (canbe x2 c)
    (canbe x3 a) (canbe x3 b)
    (canbe x4 a) (canbe x4 b)
    (canbe x5 a) (canbe x5 b) (canbe x5 c)
    ;links
    (touch x1 x3)
    (touch x2 x3)
    (touch x4 x3)
    (touch x5 x3)
    (touch x4 x5)
    ;color differences
    (diff a b)
    (diff b a)
    (diff a c)
    (diff c a)
    (diff b c)
    (diff c b)
 )

(:goal
    (AND (done x1 x3) (done x2 x3) (done x4 x3) (done x5 x3) (done x4 x5))
)
)
