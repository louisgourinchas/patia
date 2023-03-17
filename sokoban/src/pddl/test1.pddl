(define (problem sokoban-1-0)
(:domain sokoban)
(:objects 
    a b c d e f g h i j k - pos
    p1 - player
)

;1x3 empty cases surrounded by walls
(:INIT
    ;walls
    (iswall a)
    (iswall b)
    (iswall c)
    (iswall d)
    (iswall h)
    (iswall i)
    (iswall j)
    (iswall k)
    ;empty space
    (isempty e)
    (isempty f)
    ;player
    (ison p1 g)
    ;links
    (Hlink a b)
    (Hlink b a)

    (Hlink b c)
    (Hlink c b)
    
    (Vlink a e)
    (Vlink e a)
    
    (Vlink b f)
    (Vlink f b)
    
    (Vlink c g)
    (Vlink g c)
    
    (Hlink d e)
    (Vlink e d)
    
    (Hlink e f)
    (Vlink f e)
    
    (Hlink f g)
    (Vlink g f)
    
    (Hlink g h)
    (Vlink h g)
    
    (Vlink e i)
    (Vlink i e)
    
    (Vlink f j)
    (Vlink j f)
    
    (Vlink g k)
    (Vlink k g)
    
    (Hlink i j)
    (Vlink j i)
    
    (Hlink j k)
    (Vlink k j)
 )

;player moved to the right twice
(:goal
    (AND (ison p1 e))
)
)
