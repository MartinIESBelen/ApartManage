import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormularioTransaccion } from './formulario-transaccion';

describe('FormularioTransaccion', () => {
  let component: FormularioTransaccion;
  let fixture: ComponentFixture<FormularioTransaccion>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormularioTransaccion],
    }).compileComponents();

    fixture = TestBed.createComponent(FormularioTransaccion);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
