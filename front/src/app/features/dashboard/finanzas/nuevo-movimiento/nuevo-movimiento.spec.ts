import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NuevoMovimiento } from './nuevo-movimiento';

describe('NuevoMovimiento', () => {
  let component: NuevoMovimiento;
  let fixture: ComponentFixture<NuevoMovimiento>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NuevoMovimiento],
    }).compileComponents();

    fixture = TestBed.createComponent(NuevoMovimiento);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
