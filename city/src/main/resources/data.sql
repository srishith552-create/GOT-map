
INSERT INTO region (id, name, description, parent_region_id) VALUES (1, 'The North', 'Ruled by House Stark', NULL);
INSERT INTO region (id, name, description, parent_region_id) VALUES (2, 'The Crownlands', 'Land directly ruled by the Iron Throne', NULL);
INSERT INTO region (id, name, description, parent_region_id) VALUES (3, 'The Stormlands', 'Ruled by House Baratheon', NULL);


INSERT INTO city (id, name, x, y, population, region_id) VALUES (1, 'Winterfell', 120, 300, 20000, 1);
INSERT INTO city (id, name, x, y, population, region_id) VALUES (2, 'White Harbor', 180, 280, 15000, 1);
INSERT INTO city (id, name, x, y, population, region_id) VALUES (3, 'Deepwood Motte', 90, 270, 8000, 1);
INSERT INTO city (id, name, x, y, population, region_id) VALUES (4, 'Kings Landing', 400, 600, 500000, 2);
INSERT INTO city (id, name, x, y, population, region_id) VALUES (5, 'Storms End', 450, 650, 25000, 3);


INSERT INTO road (id, from_city_id, to_city_id, distance, terrain) VALUES (1, 1, 2, 90, 'road');
INSERT INTO road (id, from_city_id, to_city_id, distance, terrain) VALUES (2, 1, 3, 300, 'road');
INSERT INTO road (id, from_city_id, to_city_id, distance, terrain) VALUES (3, 1, 4, 1460, 'kingsroad');
INSERT INTO road (id, from_city_id, to_city_id, distance, terrain) VALUES (4, 4, 5, 385, 'road');


SELECT setval('region_id_seq', (SELECT MAX(id) FROM region));
SELECT setval('city_id_seq', (SELECT MAX(id) FROM city));
SELECT setval('road_id_seq', (SELECT MAX(id) FROM road));