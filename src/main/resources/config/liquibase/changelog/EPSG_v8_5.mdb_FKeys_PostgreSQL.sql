ALTER TABLE EPSG.coordoperation ADD CONSTRAINT fk_uom_code_target_coord_diff FOREIGN KEY 
( uom_code_target_coord_diff ) REFERENCES EPSG.unitofmeasure ( uom_code ) ; 

ALTER TABLE EPSG.coordoperationparamusage ADD CONSTRAINT fk_parameter_code FOREIGN KEY 
( parameter_code ) REFERENCES EPSG.coordoperationparam ( parameter_code ) ; 

ALTER TABLE EPSG.unitofmeasure ADD CONSTRAINT fk_target_uom_code FOREIGN KEY 
( target_uom_code ) REFERENCES EPSG.unitofmeasure ( uom_code ) ; 

ALTER TABLE EPSG.deprecation ADD CONSTRAINT fk_change_id FOREIGN KEY 
( change_id ) REFERENCES EPSG.change ( change_id ) ; 

ALTER TABLE EPSG.datum ADD CONSTRAINT fk_ellipsoid_code FOREIGN KEY 
( ellipsoid_code ) REFERENCES EPSG.ellipsoid ( ellipsoid_code ) ; 

ALTER TABLE EPSG.primemeridian ADD CONSTRAINT fk_uom_code FOREIGN KEY 
( uom_code ) REFERENCES EPSG.unitofmeasure ( uom_code ) ; 

ALTER TABLE EPSG.coordinateaxis ADD CONSTRAINT fk_uom_code2 FOREIGN KEY 
( uom_code ) REFERENCES EPSG.unitofmeasure ( uom_code ) ; 

ALTER TABLE EPSG.coordinatereferencesystem ADD CONSTRAINT fk_cmpd_horizcrs_code FOREIGN KEY 
( cmpd_horizcrs_code ) REFERENCES EPSG.coordinatereferencesystem ( coord_ref_sys_code ) ; 

ALTER TABLE EPSG.coordoperation ADD CONSTRAINT fk_coord_op_method_code FOREIGN KEY 
( coord_op_method_code ) REFERENCES EPSG.coordoperationmethod ( coord_op_method_code ) ; 

ALTER TABLE EPSG.coordinatereferencesystem ADD CONSTRAINT fk_datum_code FOREIGN KEY 
( datum_code ) REFERENCES EPSG.datum ( datum_code ) ; 

ALTER TABLE EPSG.datum ADD CONSTRAINT fk_prime_meridian_code FOREIGN KEY 
( prime_meridian_code ) REFERENCES EPSG.primemeridian ( prime_meridian_code ) ; 

ALTER TABLE EPSG.coordoperationparamvalue ADD CONSTRAINT fk_uom_code3 FOREIGN KEY 
( uom_code ) REFERENCES EPSG.unitofmeasure ( uom_code ) ; 

ALTER TABLE EPSG.coordinatereferencesystem ADD CONSTRAINT fk_area_of_use_code FOREIGN KEY 
( area_of_use_code ) REFERENCES EPSG.area ( area_code ) ; 

ALTER TABLE EPSG.coordinateaxis ADD CONSTRAINT fk_coord_axis_name_code FOREIGN KEY 
( coord_axis_name_code ) REFERENCES EPSG.coordinateaxisname ( coord_axis_name_code ) ; 

ALTER TABLE EPSG.coordoperation ADD CONSTRAINT fk_uom_code_source_coord_diff FOREIGN KEY 
( uom_code_source_coord_diff ) REFERENCES EPSG.unitofmeasure ( uom_code ) ; 

ALTER TABLE EPSG.coordoperationparamvalue ADD CONSTRAINT fk_parameter_codecoord_op_meth FOREIGN KEY 
( parameter_code, coord_op_method_code ) REFERENCES EPSG.coordoperationparamusage ( parameter_code, coord_op_method_code ) ; 

ALTER TABLE EPSG.coordoperation ADD CONSTRAINT fk_source_crs_code FOREIGN KEY 
( source_crs_code ) REFERENCES EPSG.coordinatereferencesystem ( coord_ref_sys_code ) ; 

ALTER TABLE EPSG.coordoperationpath ADD CONSTRAINT fk_concat_operation_code FOREIGN KEY 
( concat_operation_code ) REFERENCES EPSG.coordoperation ( coord_op_code ) ; 

ALTER TABLE EPSG.coordinateaxis ADD CONSTRAINT fk_coord_sys_code FOREIGN KEY 
( coord_sys_code ) REFERENCES EPSG.coordinatesystem ( coord_sys_code ) ; 

ALTER TABLE EPSG.coordoperationpath ADD CONSTRAINT fk_single_operation_code FOREIGN KEY 
( single_operation_code ) REFERENCES EPSG.coordoperation ( coord_op_code ) ; 

ALTER TABLE EPSG.coordinatereferencesystem ADD CONSTRAINT fk_coord_sys_code2 FOREIGN KEY 
( coord_sys_code ) REFERENCES EPSG.coordinatesystem ( coord_sys_code ) ; 

ALTER TABLE EPSG.coordinatereferencesystem ADD CONSTRAINT fk_cmpd_vertcrs_code FOREIGN KEY 
( cmpd_vertcrs_code ) REFERENCES EPSG.coordinatereferencesystem ( coord_ref_sys_code ) ; 

ALTER TABLE EPSG.coordinatereferencesystem ADD CONSTRAINT fk_source_geogcrs_code FOREIGN KEY 
( source_geogcrs_code ) REFERENCES EPSG.coordinatereferencesystem ( coord_ref_sys_code ) ; 

ALTER TABLE EPSG.coordoperation ADD CONSTRAINT fk_target_crs_code FOREIGN KEY 
( target_crs_code ) REFERENCES EPSG.coordinatereferencesystem ( coord_ref_sys_code ) ; 

ALTER TABLE EPSG.datum ADD CONSTRAINT fk_area_of_use_code2 FOREIGN KEY 
( area_of_use_code ) REFERENCES EPSG.area ( area_code ) ; 

ALTER TABLE EPSG.alias ADD CONSTRAINT fk_naming_system_code FOREIGN KEY 
( naming_system_code ) REFERENCES EPSG.namingsystem ( naming_system_code ) ; 

ALTER TABLE EPSG.coordoperation ADD CONSTRAINT fk_area_of_use_code3 FOREIGN KEY 
( area_of_use_code ) REFERENCES EPSG.area ( area_code ) ; 

ALTER TABLE EPSG.coordoperationparamvalue ADD CONSTRAINT fk_coord_op_code FOREIGN KEY 
( coord_op_code ) REFERENCES EPSG.coordoperation ( coord_op_code ) ; 

ALTER TABLE EPSG.ellipsoid ADD CONSTRAINT fk_uom_code4 FOREIGN KEY 
( uom_code ) REFERENCES EPSG.unitofmeasure ( uom_code ) ; 

ALTER TABLE EPSG.coordoperationparamusage ADD CONSTRAINT fk_coord_op_method_code2 FOREIGN KEY 
( coord_op_method_code ) REFERENCES EPSG.coordoperationmethod ( coord_op_method_code ) ; 

