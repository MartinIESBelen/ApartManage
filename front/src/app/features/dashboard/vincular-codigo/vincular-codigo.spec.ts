import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VincularCodigoComponent } from './vincular-codigo';

describe('VincularCodigo', () => {
  let component: VincularCodigoComponent;
  let fixture: ComponentFixture<VincularCodigoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VincularCodigoComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(VincularCodigoComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
