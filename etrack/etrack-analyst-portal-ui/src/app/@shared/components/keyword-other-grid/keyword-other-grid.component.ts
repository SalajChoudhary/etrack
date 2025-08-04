import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-keyword-other-grid',
  templateUrl: './keyword-other-grid.component.html',
  styleUrls: ['./keyword-other-grid.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class KeywordOtherGridComponent implements OnInit {

  @Input() otherKeywordItems: any[] = [];
  @Input() checkBoxKey: string = '';
  @Input() rounded: boolean = false;
  @Output() changedTimes: EventEmitter<any> = new EventEmitter();

  constructor() { }

  ngOnInit(): void {
  }

  emitChanges(ev: any) {
    this.changedTimes.emit(ev);
  }
  

 

}
