import { ComponentFixture, TestBed } from '@angular/core/testing';

import { unautorized } from './unautorized';

describe('unautorized', () => {
  let component: unautorized;
  let fixture: ComponentFixture<unautorized>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [unautorized]
    })
    .compileComponents();

    fixture = TestBed.createComponent(unautorized);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
