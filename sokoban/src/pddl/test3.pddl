(define (problem sokoban-1-0)
(:domain sokoban)
(:objects 
    a b c d e f g h i j k l m n o p q r s t u - pos
    p1 - player
)

;3x3 room:
;player is top left
;box in the middle
;objective is bottom right
(:INIT
    ;walls
    (iswall a)
    (iswall b)
    (iswall c)
    (iswall d)
    (iswall h)
    (iswall i)
    (iswall m)
    (iswall n)
    (iswall r)
    (iswall s)
    (iswall t)
    (iswall u)
    ;player
    (ison p1 e)
    ;crate
    (iscrate k)
    ;empty space
    (isempty f)
    (isempty g)
    (isempty j)
    (isempty l)
    (isempty o)
    (isempty p)
    (isempty q)
    ;links
    (Hlink a b) (Hlink b a)
    (Hlink b c) (Hlink b c)

    (Vlink a e) (Vlink e a)
    (Vlink b f) (Vlink f b)
    (Vlink c g) (Vlink g c)

    (Hlink d e) (Hlink e d)
    (Hlink e f) (Hlink f e)
    (Hlink f g) (Hlink g f)
    (Hlink g h) (Hlink h g)
    
    (Vlink d i) (Vlink i d)
    (Vlink e j) (Vlink j e)
    (Vlink f k) (Vlink k f)
    (Vlink g l) (Vlink l g)
    (Vlink h m) (Vlink m h)

    (Hlink i j) (Hlink j i)
    (Hlink j k) (Hlink j k)
    (Hlink k l) (Hlink l k)
    (Hlink l m) (Hlink m l)

    (Vlink i n) (Vlink n i)
    (Vlink j o) (Vlink o j)
    (Vlink k p) (Vlink p k)
    (Vlink l q) (Vlink q l)
    (Vlink m r) (Vlink r m)

    (Hlink n o) (Hlink o n)
    (Hlink o p) (Hlink p o)
    (Hlink p q) (Hlink q p)
    (Hlink q r) (Hlink r q)

    (Vlink o s) (Vlink s o)
    (Vlink p t) (Vlink t p)
    (Vlink q u) (Vlink u q)

    (Hlink s t) (Hlink t s)
    (Hlink t u) (Hlink u t)
 )

;player moved the crate to the right
(:goal
    (AND (iscrate q))
)
)
