(define (problem HANOI-1-0)
(:domain HANOI)
(:objects
    small medium big - ring
    s1 s2 s3 - spike
)

;initial config: small on s1, medium on s2, big on s3
(:INIT
    (handempty)
    (bigger big small) (bigger big medium) (bigger medium small)
    (clear small)
    (onspike small s1) (onspike medium s2) (onspike big s3)
 )

;goal: all rings stacked in order of size on s2
(:goal
    (AND (clearspike s1) (clearspike s3) (on small medium) (on medium big) (onspike big s2))
)
)
