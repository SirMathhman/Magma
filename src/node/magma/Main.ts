/*
ppaacckkaaggee  mmaaggmmaa;;
*//*
iimmppoorrtt  jjaavvaa..iioo..IIOOEExxcceeppttiioonn;;
*//*
iimmppoorrtt  jjaavvaa..nniioo..ffiillee..FFiilleess;;
*//*
iimmppoorrtt  jjaavvaa..nniioo..ffiillee..PPaatthh;;
*//*
iimmppoorrtt  jjaavvaa..nniioo..ffiillee..PPaatthhss;;
*//*
iimmppoorrtt  jjaavvaa..uuttiill..CCoolllleeccttiioonn;;
*//*
ppuubblliicc  ffiinnaall  ccllaassss  MMaaiinn  {{

		pprriivvaattee  MMaaiinn(())  {{}}



		pprriivvaattee  ssttaattiicc  SSttrriinngg  wwrraappIInnCCoommmmeenntt((ffiinnaall  SSttrriinngg  ccoonntteenntt))  {{

				rreettuurrnn  ""//**""  ++  SSyysstteemm..lliinneeSSeeppaarraattoorr(())  ++  ccoonntteenntt  ++  SSyysstteemm..lliinneeSSeeppaarraattoorr(())  ++  ""**//"";;

		}}



		pprriivvaattee  ssttaattiicc  SSttrriinngg  ccoommppiilleeRRoooott((ffiinnaall  SSttrriinngg  iinnppuutt))  {{

				rreettuurrnn  SSttrriinngg..jjooiinn(("""",,  MMaaiinn..ddiivviiddee((iinnppuutt))..ssttrreeaamm(())..mmaapp((MMaaiinn::::ccoommppiilleeRRoooottSSeeggmmeenntt))..ttooLLiisstt(())));;

		}}



		pprriivvaattee  ssttaattiicc  CCoolllleeccttiioonn<<SSttrriinngg>>  ddiivviiddee((ffiinnaall  SSttrriinngg  iinnppuutt))  {{

				DDiivviiddeeSSttaattee  ccuurrrreenntt  ==  nneeww  MMuuttaabblleeDDiivviiddeeSSttaattee((iinnppuutt));;

				wwhhiillee  ((ttrruuee))  {{

						ffiinnaall  vvaarr  mmaayybbeeNNeexxtt  ==  ccuurrrreenntt..ppoopp(());;

						iiff  ((mmaayybbeeNNeexxtt..iissEEmmppttyy(())))  bbrreeaakk;;

						ffiinnaall  vvaarr  nneexxtt  ==  mmaayybbeeNNeexxtt..ggeett(());;

						ccuurrrreenntt  ==  MMaaiinn..ffoolldd((nneexxtt..lleefftt(()),,  nneexxtt..rriigghhtt(())));;

				}}



				rreettuurrnn  ccuurrrreenntt..aaddvvaannccee(())..ssttrreeaamm(())..ttooLLiisstt(());;

		}}



		pprriivvaattee  ssttaattiicc  DDiivviiddeeSSttaattee  ffoolldd((ffiinnaall  DDiivviiddeeSSttaattee  ssttaattee,,  ffiinnaall  cchhaarr  cc))  {{

				ffiinnaall  vvaarr  aappppeennddeedd  ==  ssttaattee..aappppeenndd((cc));;

				iiff  ((''{{''  ====  cc))  rreettuurrnn  aappppeennddeedd..eenntteerr(());;

				eellssee  iiff  ((''}}''  ====  cc))  rreettuurrnn  aappppeennddeedd..eexxiitt(());;

				eellssee  iiff  (('';;''  ====  cc  &&&&  aappppeennddeedd..iissLLeevveell(())))

						rreettuurrnn  aappppeennddeedd..aaddvvaannccee(());;

				rreettuurrnn  aappppeennddeedd;;

		}}



		pprriivvaattee  ssttaattiicc  SSttrriinngg  ccoommppiilleeRRoooottSSeeggmmeenntt((ffiinnaall  SSttrriinngg  iinnppuutt))  {{

				ffiinnaall  vvaarr  ssttrriipp  ==  iinnppuutt..ssttrriipp(());;

				iiff  ((ssttrriipp..ssttaarrttssWWiitthh((""ppaacckkaaggee  ""))  ||||  ssttrriipp..ssttaarrttssWWiitthh((""iimmppoorrtt  ""))))  rreettuurrnn  """";;

				rreettuurrnn  MMaaiinn..wwrraappIInnCCoommmmeenntt((ssttrriipp));;

		}}



		ppuubblliicc  ssttaattiicc  vvooiidd  mmaaiinn((ffiinnaall  SSttrriinngg[[]]  aarrggss))  {{

				ttrryy  {{

						ffiinnaall  SSttrriinngg  ccoonntteenntt  ==  FFiilleess..rreeaaddSSttrriinngg((PPaatthhss..ggeett((""ssrrcc//jjaavvaa//mmaaggmmaa//MMaaiinn..jjaavvaa""))));;

						ffiinnaall  PPaatthh  ttaarrggeettPPaatthh  ==  PPaatthh..ooff((""..//ssrrcc//nnooddee//mmaaggmmaa//MMaaiinn..ttss""));;

						FFiilleess..ccrreeaatteeDDiirreeccttoorriieess((ttaarrggeettPPaatthh..ggeettPPaarreenntt(())));;

						FFiilleess..wwrriitteeSSttrriinngg((ttaarrggeettPPaatthh,,  MMaaiinn..ccoommppiilleeRRoooott((ccoonntteenntt))));;

				}}  ccaattcchh  ((ffiinnaall  IIOOEExxcceeppttiioonn  ee))  {{

						SSyysstteemm..eerrrr..pprriinnttllnn((""EErrrroorr  ccooppyyiinngg  ffiillee::  ""  ++  ee..ggeettMMeessssaaggee(())));;

				}}

		}}

}}
*/