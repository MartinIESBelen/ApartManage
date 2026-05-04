import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectorPropiedad } from './selector-propiedad';

describe('SelectorPropiedad', () => {
  let component: SelectorPropiedad;
  let fixture: ComponentFixture<SelectorPropiedad>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectorPropiedad],
    }).compileComponents();

    fixture = TestBed.createComponent(SelectorPropiedad);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
