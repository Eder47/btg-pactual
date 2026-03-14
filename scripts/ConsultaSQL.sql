
SELECT DISTINCT c.nombre, c.apellidos
FROM Cliente c
INNER JOIN Inscripcion i ON c.id = i.idCliente
INNER JOIN Producto p ON i.idProducto = p.id
WHERE EXISTS (
    
    SELECT 1
    FROM Disponibilidad d
    WHERE d.idProducto = p.id
    AND NOT EXISTS (
        SELECT 1
        FROM Disponibilidad d2
        WHERE d2.idProducto = p.id
        AND d2.idSucursal NOT IN (
            SELECT v.idSucursal
            FROM Visitan v
            WHERE v.idCliente = c.id
        )
    )
)
AND EXISTS (
    SELECT 1
    FROM Visitan v
    INNER JOIN Disponibilidad d ON v.idSucursal = d.idSucursal
    WHERE v.idCliente = c.id
    AND d.idProducto = p.id
);