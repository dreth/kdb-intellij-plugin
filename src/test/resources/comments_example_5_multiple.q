// JSON alterations should not be accumulating. Only the last set
// of modifications should be applied to the baseline JSON that was loaded from config.
//
// JSON alterations should not be persistent between IntelliJ sessions -
// so that if I close IntelliJ and start it again, I get the graph specs as
// in the original JSON.
//
// For example:

// This should start with the default configuration as in the original JSON.
t: 1e-2*til 1000;
([] t; sinT: sin t; shiftedSinT: sin t+2)

// This should only modify the title of the graph.
// {"title": {"show": true, "text": "Several sine waves", "font": {"size": 20}}}
([] t; sinT: sin t; shiftedSinT: sin t+2)

// Now I'm modifying only the range axis - so the title should be empty,
// as specified in the original JSON.
// {"rangeAxis": {"label": { "show": true, "text": "Range - tan(t)"}}}
([] t; tanT: tan t; atanT: atan t+2)

// And this one should modify just the domain axis. Title and range axis
// should be empty, as in the original JSON from the configuration.
// {"domainAxis": {"label": { "show": true, "text": "Tan and equivalents"}}}
([] t; tanT: tan t; atanT: atan t+2)

// {"domainAxis":
//      {"label":
//          { "show": true, "text": "Time"}
//      }
// }
([] t; tanT: tan t; atanT: atan t+2)

// And this currently throws an error on JSON parsing.
// Or just fails silently without error messages.
// {"series": [
//        {
//        "lineWidth": 1,
//        "color": "#FFA9F2",
//        "lineType" : "SOLID",
//        "visibleInLegend": true,
//        "show": true
//        },
//        {
//        "lineWidth": 1,
//        "color": "#FF2B4C",
//        "lineType" : "SOLID",
//        "visibleInLegend": true,
//        "show": true
//        }
//    ]}
([] t; tanT: tan t; atanT: atan t+2)

