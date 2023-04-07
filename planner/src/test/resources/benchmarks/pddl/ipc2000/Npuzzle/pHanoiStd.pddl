(define (problem HANOI-1-0)
(:domain HANOI)
(:objects
    1 2 3 4 5 6 7 8 - square
    a b c d e f g h i - pos
)

;initial config: everything in it's place execpt h free, 8 on i
(:INIT
    (free h)
    (permutable a b)
    (permutable a d)
    (permutable a b)
 )

;goal: all rings stacked in order of size on s3
(:goal
    (AND (clearspike s1) (clearspike s2) (onspike big s3) (on small medium) (on medium big))
)
)
