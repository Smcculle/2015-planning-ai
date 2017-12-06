# 2015-planning-ai
Class projects from Planning in AI.  Teams of students each contributed a single planner.  I worked by myself to implement LPG, which uses local search to solve planning graphs.  

LPG's strengths include leveraging structure of planning graphs in its heuristics and the ability to handle action costs.  The search scheme is similar to Walksat but uses the planning graph as the search space.  

My implementation of LPG solved 12 problems and was ranked 8th out of 12: 

<body>
<h1>Planner Benchmark Results</h1>
<h2>Problems Solved</h2><table>
<tr><th></th><th>SHSP</th><th>FF</th><th>SGP</th><th>SPOP</th><th>HSP</th><th>IW2</th><th>BFS</th><th>LPG</th><th>DPLL Planner</th><th>FD</th><th>GP</th><th>POP</th><th>Total</th></tr>
<tr><th>do_nothing</th><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>12</td></tr>
<tr><th>easy_stack</th><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>12</td></tr>
<tr><th>easy_unstack</th><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>0</td><td>1</td><td>11</td></tr>
<tr><th>sussman</th><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>12</td></tr>
<tr><th>have_eat_cake</th><td>1</td><td>1</td><td>1</td><td>0</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>0</td><td>1</td><td>0</td><td>9</td></tr>
<tr><th>reverse_2</th><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>0</td><td>1</td><td>1</td><td>11</td></tr>
<tr><th>reverse_4</th><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>0</td><td>0</td><td>0</td><td>9</td></tr>
<tr><th>reverse_6</th><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>5</td></tr>
<tr><th>reverse_8</th><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>5</td></tr>
<tr><th>reverse_10</th><td>1</td><td>1</td><td>1</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>3</td></tr>
<tr><th>reverse_12</th><td>1</td><td>1</td><td>1</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>3</td></tr>
<tr><th>deliver_1</th><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>12</td></tr>
<tr><th>deliver_2</th><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>0</td><td>11</td></tr>
<tr><th>deliver_3</th><td>1</td><td>1</td><td>0</td><td>1</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>3</td></tr>
<tr><th>deliver_4</th><td>1</td><td>1</td><td>0</td><td>1</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>3</td></tr>
<tr><th>deliver_return_1</th><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>0</td><td>0</td><td>10</td></tr>
<tr><th>deliver_return_2</th><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>0</td><td>0</td><td>10</td></tr>
<tr><th>deliver_return_3</th><td>1</td><td>1</td><td>0</td><td>1</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>3</td></tr>
<tr><th>deliver_return_4</th><td>1</td><td>1</td><td>0</td><td>1</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>3</td></tr>
<tr><th>easy_wumpus</th><td>1</td><td>1</td><td>1</td><td>0</td><td>1</td><td>1</td><td>1</td><td>1</td><td>0</td><td>0</td><td>0</td><td>0</td><td>7</td></tr>
<tr><th>medium_wumpus</th><td>1</td><td>1</td><td>1</td><td>0</td><td>1</td><td>1</td><td>1</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>6</td></tr>
<tr><th>hard_wumpus</th><td>1</td><td>0</td><td>0</td><td>0</td><td>0</td><td>1</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>2</td></tr>
<tr><th>Total</th><td>22</td><td>21</td><td>17</td><td>16</td><td>15</td><td>14</td><td>13</td><td>12</td><td>11</td><td>8</td><td>7</td><td>6</td><td></td></tr>
</table>
<h2>Nodes Visited</h2><table>
<tr><th></th><th>FD</th><th>SHSP</th><th>FF</th><th>SPOP</th><th>SGP</th><th>HSP</th><th>DPLL Planner</th><th>LPG</th><th>IW2</th><th>BFS</th><th>POP</th><th>GP</th><th>Average</th></tr>
<tr><th>do_nothing</th><td>0</td><td>1</td><td>1</td><td>0</td><td>0</td><td>1</td><td>20</td><td>0</td><td>1</td><td>1</td><td>1</td><td>0</td><td>2</td></tr>
<tr><th>easy_stack</th><td>1</td><td>2</td><td>2</td><td>3</td><td>1</td><td>2</td><td>1</td><td>1</td><td>3</td><td>3</td><td>4</td><td>406</td><td>36</td></tr>
<tr><th>easy_unstack</th><td>1</td><td>2</td><td>2</td><td>2</td><td>1</td><td>2</td><td>1</td><td>1</td><td>2</td><td>2</td><td>3</td><td>10000</td><td>835</td></tr>
<tr><th>sussman</th><td>4</td><td>4</td><td>18</td><td>17</td><td>7</td><td>4</td><td>90</td><td>5</td><td>39</td><td>41</td><td>15</td><td>5120</td><td>447</td></tr>
<tr><th>have_eat_cake</th><td>0</td><td>3</td><td>3</td><td>1</td><td>2</td><td>3</td><td>2</td><td>2</td><td>3</td><td>3</td><td>2</td><td>6</td><td>3</td></tr>
<tr><th>reverse_2</th><td>0</td><td>3</td><td>3</td><td>6</td><td>2</td><td>3</td><td>2</td><td>3</td><td>5</td><td>5</td><td>8</td><td>36</td><td>6</td></tr>
<tr><th>reverse_4</th><td>0</td><td>7</td><td>12</td><td>32</td><td>14</td><td>8</td><td>5666</td><td>7202</td><td>423</td><td>507</td><td>10000</td><td>10000</td><td>2823</td></tr>
<tr><th>reverse_6</th><td>0</td><td>11</td><td>18</td><td>93</td><td>39</td><td>25</td><td>10002</td><td>10000</td><td>10000</td><td>10000</td><td>10000</td><td>10001</td><td>5016</td></tr>
<tr><th>reverse_8</th><td>0</td><td>15</td><td>24</td><td>2647</td><td>113</td><td>314</td><td>10002</td><td>5603</td><td>10000</td><td>10000</td><td>10000</td><td>10001</td><td>4893</td></tr>
<tr><th>reverse_10</th><td>0</td><td>19</td><td>30</td><td>10000</td><td>334</td><td>10000</td><td>2</td><td>0</td><td>10000</td><td>10000</td><td>10000</td><td>0</td><td>4199</td></tr>
<tr><th>reverse_12</th><td>0</td><td>23</td><td>36</td><td>10000</td><td>989</td><td>10000</td><td>734</td><td>0</td><td>10000</td><td>10000</td><td>10000</td><td>0</td><td>4315</td></tr>
<tr><th>deliver_1</th><td>3</td><td>4</td><td>13</td><td>8</td><td>5</td><td>4</td><td>12</td><td>11</td><td>5</td><td>19</td><td>9</td><td>93</td><td>16</td></tr>
<tr><th>deliver_2</th><td>5</td><td>6</td><td>65</td><td>15</td><td>13</td><td>13</td><td>1259</td><td>41</td><td>24</td><td>297</td><td>1443</td><td>763</td><td>329</td></tr>
<tr><th>deliver_3</th><td>0</td><td>10</td><td>292</td><td>541</td><td>10000</td><td>10000</td><td>10003</td><td>10000</td><td>10000</td><td>10000</td><td>10000</td><td>10000</td><td>6737</td></tr>
<tr><th>deliver_4</th><td>0</td><td>13</td><td>1724</td><td>7360</td><td>10000</td><td>10000</td><td>10003</td><td>10000</td><td>10000</td><td>10000</td><td>10000</td><td>10001</td><td>7425</td></tr>
<tr><th>deliver_return_1</th><td>5</td><td>6</td><td>35</td><td>24</td><td>118</td><td>5</td><td>57</td><td>614</td><td>6</td><td>53</td><td>1866</td><td>10001</td><td>1066</td></tr>
<tr><th>deliver_return_2</th><td>6</td><td>7</td><td>136</td><td>38</td><td>602</td><td>15</td><td>2587</td><td>4618</td><td>37</td><td>1080</td><td>10000</td><td>10001</td><td>2427</td></tr>
<tr><th>deliver_return_3</th><td>0</td><td>61</td><td>2563</td><td>633</td><td>10000</td><td>10000</td><td>10003</td><td>10000</td><td>10000</td><td>10000</td><td>10000</td><td>10001</td><td>6938</td></tr>
<tr><th>deliver_return_4</th><td>0</td><td>153</td><td>3466</td><td>8077</td><td>10000</td><td>10000</td><td>10003</td><td>10000</td><td>10000</td><td>10000</td><td>10000</td><td>10001</td><td>7642</td></tr>
<tr><th>easy_wumpus</th><td>0</td><td>4</td><td>8</td><td>5</td><td>5</td><td>5</td><td>1</td><td>204</td><td>12</td><td>17</td><td>50</td><td>10000</td><td>859</td></tr>
<tr><th>medium_wumpus</th><td>0</td><td>16</td><td>428</td><td>5</td><td>3355</td><td>341</td><td>4</td><td>10000</td><td>68</td><td>643</td><td>50</td><td>10000</td><td>2076</td></tr>
<tr><th>hard_wumpus</th><td>0</td><td>165</td><td>10000</td><td>5</td><td>10000</td><td>2650</td><td>8</td><td>2312</td><td>258</td><td>10000</td><td>50</td><td>10000</td><td>3787</td></tr>
<tr><th>Average</th><td>1</td><td>24</td><td>858</td><td>1796</td><td>2527</td><td>2882</td><td>3203</td><td>3664</td><td>3677</td><td>4212</td><td>4705</td><td>6201</td><td></td></tr>
</table>
<h2>Nodes Expanded</h2><table>
<tr><th></th><th>FD</th><th>SHSP</th><th>FF</th><th>SGP</th><th>SPOP</th><th>DPLL Planner</th><th>POP</th><th>GP</th><th>LPG</th><th>HSP</th><th>IW2</th><th>BFS</th><th>Average</th></tr>
<tr><th>do_nothing</th><td>0</td><td>1</td><td>0</td><td>0</td><td>2</td><td>0</td><td>2</td><td>0</td><td>0</td><td>1</td><td>1</td><td>1</td><td>1</td></tr>
<tr><th>easy_stack</th><td>2</td><td>5</td><td>1</td><td>1</td><td>7</td><td>0</td><td>7</td><td>406</td><td>2</td><td>5</td><td>6</td><td>6</td><td>37</td></tr>
<tr><th>easy_unstack</th><td>1</td><td>5</td><td>1</td><td>1</td><td>5</td><td>0</td><td>5</td><td>10000</td><td>2</td><td>5</td><td>5</td><td>5</td><td>836</td></tr>
<tr><th>sussman</th><td>13</td><td>16</td><td>17</td><td>7</td><td>29</td><td>88</td><td>25</td><td>5120</td><td>15</td><td>16</td><td>107</td><td>109</td><td>464</td></tr>
<tr><th>have_eat_cake</th><td>0</td><td>3</td><td>2</td><td>2</td><td>6</td><td>0</td><td>6</td><td>6</td><td>5</td><td>3</td><td>3</td><td>3</td><td>3</td></tr>
<tr><th>reverse_2</th><td>0</td><td>6</td><td>2</td><td>2</td><td>12</td><td>0</td><td>13</td><td>36</td><td>8</td><td>6</td><td>8</td><td>8</td><td>8</td></tr>
<tr><th>reverse_4</th><td>0</td><td>38</td><td>7</td><td>14</td><td>52</td><td>5666</td><td>12292</td><td>10000</td><td>35292</td><td>39</td><td>1873</td><td>2283</td><td>5630</td></tr>
<tr><th>reverse_6</th><td>0</td><td>126</td><td>11</td><td>39</td><td>144</td><td>9998</td><td>12940</td><td>10001</td><td>55846</td><td>174</td><td>75877</td><td>73414</td><td>19881</td></tr>
<tr><th>reverse_8</th><td>0</td><td>302</td><td>15</td><td>113</td><td>4199</td><td>9998</td><td>15578</td><td>10001</td><td>35517</td><td>1951</td><td>71266</td><td>69441</td><td>18198</td></tr>
<tr><th>reverse_10</th><td>0</td><td>598</td><td>19</td><td>334</td><td>15734</td><td>0</td><td>16123</td><td>0</td><td>0</td><td>73432</td><td>71241</td><td>69428</td><td>20576</td></tr>
<tr><th>reverse_12</th><td>0</td><td>1046</td><td>23</td><td>989</td><td>15734</td><td>728</td><td>15935</td><td>0</td><td>0</td><td>73432</td><td>71241</td><td>69428</td><td>20713</td></tr>
<tr><th>deliver_1</th><td>9</td><td>12</td><td>12</td><td>5</td><td>13</td><td>10</td><td>13</td><td>93</td><td>32</td><td>12</td><td>14</td><td>53</td><td>23</td></tr>
<tr><th>deliver_2</th><td>20</td><td>24</td><td>60</td><td>13</td><td>25</td><td>1256</td><td>1665</td><td>763</td><td>112</td><td>46</td><td>85</td><td>1080</td><td>429</td></tr>
<tr><th>deliver_3</th><td>0</td><td>85</td><td>262</td><td>10000</td><td>841</td><td>10000</td><td>11617</td><td>10000</td><td>45302</td><td>80885</td><td>85268</td><td>82292</td><td>28046</td></tr>
<tr><th>deliver_4</th><td>0</td><td>146</td><td>1405</td><td>10000</td><td>12207</td><td>10000</td><td>11570</td><td>10001</td><td>51187</td><td>102265</td><td>107080</td><td>102492</td><td>34863</td></tr>
<tr><th>deliver_return_1</th><td>15</td><td>17</td><td>34</td><td>118</td><td>38</td><td>54</td><td>2096</td><td>10001</td><td>3147</td><td>14</td><td>16</td><td>147</td><td>1308</td></tr>
<tr><th>deliver_return_2</th><td>24</td><td>26</td><td>110</td><td>602</td><td>56</td><td>2584</td><td>11058</td><td>10001</td><td>19107</td><td>52</td><td>127</td><td>3928</td><td>3973</td></tr>
<tr><th>deliver_return_3</th><td>0</td><td>511</td><td>1509</td><td>10000</td><td>1001</td><td>10000</td><td>11913</td><td>10001</td><td>62888</td><td>80885</td><td>85268</td><td>82292</td><td>29689</td></tr>
<tr><th>deliver_return_4</th><td>0</td><td>1668</td><td>2085</td><td>10000</td><td>13674</td><td>10000</td><td>11674</td><td>10001</td><td>59357</td><td>102265</td><td>107080</td><td>102492</td><td>35858</td></tr>
<tr><th>easy_wumpus</th><td>0</td><td>11</td><td>7</td><td>5</td><td>5</td><td>0</td><td>49</td><td>10000</td><td>1100</td><td>11</td><td>32</td><td>46</td><td>939</td></tr>
<tr><th>medium_wumpus</th><td>0</td><td>50</td><td>427</td><td>3355</td><td>5</td><td>4</td><td>49</td><td>10000</td><td>51268</td><td>800</td><td>126</td><td>1700</td><td>5649</td></tr>
<tr><th>hard_wumpus</th><td>0</td><td>503</td><td>5473</td><td>10000</td><td>5</td><td>8</td><td>49</td><td>10000</td><td>13089</td><td>6303</td><td>546</td><td>24793</td><td>5897</td></tr>
<tr><th>Average</th><td>4</td><td>236</td><td>522</td><td>2527</td><td>2900</td><td>3200</td><td>6122</td><td>6201</td><td>19694</td><td>23755</td><td>30785</td><td>31156</td><td></td></tr>
</table>
<h2>Time (ms)</h2><table>
<tr><th></th><th>FD</th><th>BFS</th><th>SHSP</th><th>SPOP</th><th>POP</th><th>IW2</th><th>SGP</th><th>FF</th><th>GP</th><th>HSP</th><th>LPG</th><th>DPLL Planner</th><th>Average</th></tr>
<tr><th>do_nothing</th><td>2</td><td>1</td><td>1</td><td>1</td><td>3</td><td>1</td><td>5</td><td>2</td><td>0</td><td>0</td><td>1</td><td>36</td><td>4</td></tr>
<tr><th>easy_stack</th><td>4</td><td>0</td><td>3</td><td>1</td><td>4</td><td>0</td><td>1</td><td>2</td><td>93</td><td>2</td><td>2</td><td>5</td><td>10</td></tr>
<tr><th>easy_unstack</th><td>2</td><td>0</td><td>0</td><td>1</td><td>1</td><td>0</td><td>0</td><td>2</td><td>150</td><td>0</td><td>0</td><td>4</td><td>13</td></tr>
<tr><th>sussman</th><td>5</td><td>1</td><td>2</td><td>4</td><td>10</td><td>3</td><td>0</td><td>6</td><td>97</td><td>5</td><td>158</td><td>246</td><td>45</td></tr>
<tr><th>have_eat_cake</th><td>0</td><td>0</td><td>0</td><td>1</td><td>1</td><td>0</td><td>0</td><td>0</td><td>1</td><td>0</td><td>0</td><td>0</td><td>0</td></tr>
<tr><th>reverse_2</th><td>0</td><td>0</td><td>0</td><td>1</td><td>3</td><td>0</td><td>0</td><td>1</td><td>0</td><td>1</td><td>13</td><td>5</td><td>2</td></tr>
<tr><th>reverse_4</th><td>0</td><td>6</td><td>3</td><td>7</td><td>1521</td><td>36</td><td>2</td><td>6</td><td>41</td><td>14</td><td>3838</td><td>14792</td><td>1689</td></tr>
<tr><th>reverse_6</th><td>0</td><td>80</td><td>12</td><td>27</td><td>1985</td><td>1430</td><td>2</td><td>9</td><td>124</td><td>112</td><td>25833</td><td>151332</td><td>15079</td></tr>
<tr><th>reverse_8</th><td>0</td><td>76</td><td>36</td><td>1720</td><td>2995</td><td>2884</td><td>4</td><td>17</td><td>3000</td><td>8355</td><td>900057</td><td>615467</td><td>127884</td></tr>
<tr><th>reverse_10</th><td>8</td><td>91</td><td>124</td><td>5247</td><td>4635</td><td>13792</td><td>181874</td><td>42</td><td>932634</td><td>215911</td><td>900008</td><td>900000</td><td>262864</td></tr>
<tr><th>reverse_12</th><td>0</td><td>192</td><td>219</td><td>5067</td><td>4910</td><td>14276</td><td>8</td><td>37</td><td>11</td><td>210576</td><td>900000</td><td>900021</td><td>169610</td></tr>
<tr><th>deliver_1</th><td>15</td><td>6</td><td>0</td><td>1</td><td>1</td><td>1</td><td>0</td><td>1</td><td>4</td><td>1</td><td>8</td><td>7</td><td>4</td></tr>
<tr><th>deliver_2</th><td>9</td><td>14</td><td>0</td><td>2</td><td>333</td><td>1</td><td>1</td><td>8</td><td>18</td><td>3</td><td>47</td><td>451</td><td>74</td></tr>
<tr><th>deliver_3</th><td>0</td><td>43</td><td>2</td><td>106</td><td>4073</td><td>1383</td><td>29</td><td>79</td><td>44</td><td>21497</td><td>4188</td><td>34108</td><td>5463</td></tr>
<tr><th>deliver_4</th><td>0</td><td>79</td><td>8</td><td>2275</td><td>6046</td><td>2756</td><td>70</td><td>602</td><td>73</td><td>48531</td><td>10750</td><td>171372</td><td>20214</td></tr>
<tr><th>deliver_return_1</th><td>3</td><td>1</td><td>0</td><td>1</td><td>554</td><td>0</td><td>0</td><td>2</td><td>37</td><td>1</td><td>270</td><td>21</td><td>74</td></tr>
<tr><th>deliver_return_2</th><td>9</td><td>3</td><td>0</td><td>1</td><td>3745</td><td>2</td><td>1</td><td>12</td><td>48</td><td>5</td><td>1154</td><td>1147</td><td>511</td></tr>
<tr><th>deliver_return_3</th><td>0</td><td>70</td><td>11</td><td>79</td><td>4028</td><td>1441</td><td>42</td><td>521</td><td>62</td><td>21430</td><td>6865</td><td>40546</td><td>6258</td></tr>
<tr><th>deliver_return_4</th><td>0</td><td>97</td><td>96</td><td>2393</td><td>5654</td><td>2886</td><td>36</td><td>925</td><td>327</td><td>47838</td><td>13772</td><td>164709</td><td>19894</td></tr>
<tr><th>easy_wumpus</th><td>32</td><td>7</td><td>47</td><td>0</td><td>2</td><td>11</td><td>0</td><td>62</td><td>17169</td><td>339</td><td>405604</td><td>900027</td><td>110275</td></tr>
<tr><th>medium_wumpus</th><td>1</td><td>183</td><td>31</td><td>42</td><td>37</td><td>140</td><td>132</td><td>1467</td><td>1413</td><td>9069</td><td>202524</td><td>900000</td><td>92920</td></tr>
<tr><th>hard_wumpus</th><td>10</td><td>1038</td><td>5235</td><td>3</td><td>2</td><td>377</td><td>177</td><td>565935</td><td>657</td><td>901623</td><td>902693</td><td>904268</td><td>273502</td></tr>
<tr><th>Average</th><td>5</td><td>90</td><td>265</td><td>772</td><td>1843</td><td>1883</td><td>8290</td><td>25897</td><td>43455</td><td>67514</td><td>194445</td><td>259026</td><td></td></tr>
</table>
<h2>Final Ranking</h2>
<ol>
<li>SHSP</li>
<li>FF</li>
<li>SGP</li>
<li>SPOP</li>
<li>HSP</li>
<li>IW2</li>
<li>BFS</li>
<li>LPG</li>
<li>DPLL Planner</li>
<li>FD</li>
<li>GP</li>
<li>POP</li>
<ol>
</body>
