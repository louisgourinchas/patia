(define (problem HANOI-1-0)
(:domain HANOI)
(:objects
    1 2 3 4 5 - square
    a b c d e f - pos
)

;initial config: 3x2 grid with 5 squares, like so
;grid:          squares:
;a b c          X 1 2
;d e f          3 4 5
;with X marking an empty location
(:INIT
    (free f)
    (permutable a b)
    (permutable a d)
    (permutable b a)
    (permutable b c)
    (permutable b e)
    (permutable c b)
    (permutable c f)
    (permutable d a)
    (permutable d e)
    (permutable e d)
    (permutable e b)
    (permutable e f)
    (permutable f e)
    (permutable f c)
    (on 1 b)
    (on 2 c)
    (on 3 d)
    (on 4 e)
    (on 5 f)
 )

;goal is:
;1 2 3
;4 5 X
(:goal
    (AND    (on 1 a) (on 2 b) (on 3 c) (on 4 d) (on 5 e))
)
)
