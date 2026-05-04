import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReservaManualComponent } from './reserva-manual';

describe('ReservaManual', () => {
  let component: ReservaManualComponent;
  let fixture: ComponentFixture<ReservaManualComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReservaManualComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ReservaManualComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
