import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetallesContrato } from './detalles-contrato';

describe('DetallesContrato', () => {
  let component: DetallesContrato;
  let fixture: ComponentFixture<DetallesContrato>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetallesContrato],
    }).compileComponents();

    fixture = TestBed.createComponent(DetallesContrato);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
