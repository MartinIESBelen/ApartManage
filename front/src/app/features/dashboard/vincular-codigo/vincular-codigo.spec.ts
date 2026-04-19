import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VincularCodigo } from './vincular-codigo';

describe('VincularCodigo', () => {
  let component: VincularCodigo;
  let fixture: ComponentFixture<VincularCodigo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VincularCodigo],
    }).compileComponents();

    fixture = TestBed.createComponent(VincularCodigo);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
